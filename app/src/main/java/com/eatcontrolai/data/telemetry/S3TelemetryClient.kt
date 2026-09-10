package com.eatcontrolai.data.telemetry

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Cliente de telemetria anônima para envio de imagens do opt-in de pesquisa para o S3 / Railway Buckets / Cloudflare R2.
 *
 * Envia fotos de rótulos/alimentos estritamente quando o usuário autorizou nos termos de pesquisa
 * ([PrivacySettings.shareForImprovement] == true).
 * Suporta autenticação nativa AWS Signature V4 para buckets privados sem dependências externas.
 */
class S3TelemetryClient(
    var bucketName: String? = null,
    var region: String = "auto",
    var customEndpoint: String? = null,
    var accessKey: String? = null,
    var secretKey: String? = null
) {
    suspend fun uploadMealPhoto(recordId: String, imageBytes: ByteArray): Boolean = withContext(Dispatchers.IO) {
        val bucket = bucketName?.trim()
        val endpoint = customEndpoint?.trim()

        if (bucket.isNullOrBlank() && endpoint.isNullOrBlank()) {
            Log.w(TAG, "Nenhum bucket ou endpoint S3 configurado.")
            return@withContext false
        }

        runCatching {
            val host = if (!endpoint.isNullOrBlank()) {
                URL(endpoint).host
            } else {
                "$bucket.s3.$region.amazonaws.com"
            }

            val path = if (!endpoint.isNullOrBlank()) {
                if (bucket != null && !endpoint.contains(bucket)) {
                    "/$bucket/$recordId.jpg"
                } else {
                    "/$recordId.jpg"
                }
            } else {
                "/optin_dataset/$recordId.jpg"
            }

            val targetUrl = if (!endpoint.isNullOrBlank()) {
                "${endpoint.trimEnd('/')}$path"
            } else {
                "https://$host$path"
            }

            val url = URL(targetUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "PUT"
                connectTimeout = 12_000
                readTimeout = 12_000
                doOutput = true
                setRequestProperty("Host", host)
                setRequestProperty("Content-Type", "image/jpeg")
                setRequestProperty("x-amz-meta-anonymized", "true")
                setRequestProperty("x-amz-meta-source", "eatcontrol_android")
            }

            val aKey = accessKey?.trim()
            val sKey = secretKey?.trim()

            // Assinatura AWS Signature V4 para buckets privados
            if (!aKey.isNullOrBlank() && !sKey.isNullOrBlank()) {
                val now = Date()
                val amzDate = getAmzDate(now)
                val dateStamp = getDateStamp(now)
                val reg = region.takeIf { it.isNotBlank() } ?: "auto"

                val payloadHash = sha256Hex(imageBytes)
                connection.setRequestProperty("x-amz-date", amzDate)
                connection.setRequestProperty("x-amz-content-sha256", payloadHash)

                val signedHeaders = "content-type;host;x-amz-content-sha256;x-amz-date;x-amz-meta-anonymized;x-amz-meta-source"
                val canonicalHeaders = "content-type:image/jpeg\n" +
                    "host:$host\n" +
                    "x-amz-content-sha256:$payloadHash\n" +
                    "x-amz-date:$amzDate\n" +
                    "x-amz-meta-anonymized:true\n" +
                    "x-amz-meta-source:eatcontrol_android\n"

                val canonicalRequest = "PUT\n$path\n\n$canonicalHeaders\n$signedHeaders\n$payloadHash"
                val scope = "$dateStamp/$reg/s3/aws4_request"
                val stringToSign = "AWS4-HMAC-SHA256\n$amzDate\n$scope\n${sha256Hex(canonicalRequest.toByteArray(Charsets.UTF_8))}"

                val signingKey = getSignatureKey(sKey, dateStamp, reg, "s3")
                val signature = hex(hmacSha256(signingKey, stringToSign))

                val authHeader = "AWS4-HMAC-SHA256 Credential=$aKey/$scope, SignedHeaders=$signedHeaders, Signature=$signature"
                connection.setRequestProperty("Authorization", authHeader)
            }

            connection.outputStream.use { it.write(imageBytes) }
            val code = connection.responseCode
            val isSuccess = code in 200..299
            if (isSuccess) {
                Log.i(TAG, "Foto de refeição enviada com sucesso ao S3: $recordId.jpg (HTTP $code)")
            } else {
                val err = connection.errorStream?.bufferedReader()?.use { it.readText() }
                Log.w(TAG, "Falha no upload para o S3: HTTP $code - $err")
            }
            connection.disconnect()
            isSuccess
        }.getOrElse {
            Log.e(TAG, "Erro de conexão ao enviar foto para o S3", it)
            false
        }
    }

    companion object {
        private const val TAG = "S3Telemetry"

        private fun getAmzDate(date: Date): String =
            SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(date)

        private fun getDateStamp(date: Date): String =
            SimpleDateFormat("yyyyMMdd", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(date)

        private fun sha256Hex(bytes: ByteArray): String {
            val md = MessageDigest.getInstance("SHA-256")
            return hex(md.digest(bytes))
        }

        private fun hmacSha256(key: ByteArray, data: String): ByteArray {
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(SecretKeySpec(key, "HmacSHA256"))
            return mac.doFinal(data.toByteArray(Charsets.UTF_8))
        }

        private fun getSignatureKey(key: String, dateStamp: String, regionName: String, serviceName: String): ByteArray {
            val kSecret = ("AWS4$key").toByteArray(Charsets.UTF_8)
            val kDate = hmacSha256(kSecret, dateStamp)
            val kRegion = hmacSha256(kDate, regionName)
            val kService = hmacSha256(kRegion, serviceName)
            return hmacSha256(kService, "aws4_request")
        }

        private fun hex(bytes: ByteArray): String =
            bytes.joinToString("") { "%02x".format(it) }
    }
}
