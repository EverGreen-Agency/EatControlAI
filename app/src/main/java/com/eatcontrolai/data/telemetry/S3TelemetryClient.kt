package com.eatcontrolai.data.telemetry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Cliente de telemetria anônima para envio de imagens do opt-in de pesquisa para o AWS S3 / Cloudflare R2.
 *
 * Envia fotos de rótulos/alimentos estritamente quando o usuário autorizou nos termos de pesquisa
 * ([PrivacySettings.shareForImprovement] == true).
 */
class S3TelemetryClient(
    var bucketName: String? = null,
    var region: String = "sa-east-1",
    var customEndpoint: String? = null
) {
    suspend fun uploadMealPhoto(recordId: String, imageBytes: ByteArray): Boolean = withContext(Dispatchers.IO) {
        val bucket = bucketName?.trim()
        val endpoint = customEndpoint?.trim()

        if (bucket.isNullOrBlank() && endpoint.isNullOrBlank()) {
            // Nenhuma infraestrutura de S3 configurada ainda
            return@withContext false
        }

        runCatching {
            val targetUrl = if (!endpoint.isNullOrBlank()) {
                if (bucket != null && !endpoint.contains(bucket)) {
                    "${endpoint.trimEnd('/')}/$bucket/$recordId.jpg"
                } else {
                    "${endpoint.trimEnd('/')}/$recordId.jpg"
                }
            } else {
                "https://$bucket.s3.$region.amazonaws.com/optin_dataset/$recordId.jpg"
            }

            val url = URL(targetUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "PUT"
                connectTimeout = 10_000
                readTimeout = 10_000
                doOutput = true
                setRequestProperty("Content-Type", "image/jpeg")
                setRequestProperty("x-amz-meta-anonymized", "true")
                setRequestProperty("x-amz-meta-source", "eatcontrol_android")
            }

            connection.outputStream.use { it.write(imageBytes) }
            val code = connection.responseCode
            connection.disconnect()
            code in 200..299
        }.getOrDefault(false)
    }
}
