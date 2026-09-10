package com.eatcontrolai.inference.cloud

import android.util.Base64
import com.eatcontrolai.inference.Detection
import com.eatcontrolai.inference.DetectionResult
import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.ObjectDetectionProvider
import com.eatcontrolai.inference.mlkit.MlKitImageLabelingProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Provedor de visão em nuvem multimodal para identificação precisa de pratos e ingredientes.
 *
 * Suporta chaves de API gratuitas:
 * - Google Gemini (gemini-1.5-flash): 15 RPM no tier gratuito do Google AI Studio.
 * - OpenRouter (compatível com OpenAI): suporta modelos multimodais livres como `google/gemini-2.0-flash-exp:free`.
 *
 * Se nenhuma chave estiver definida ou se o aparelho estiver offline, faz fallback imediato e transparente
 * para o [MlKitImageLabelingProvider] local.
 */
class CloudVisionProvider(
    var geminiApiKey: String? = null,
    var openRouterApiKey: String? = null,
    var groqApiKey: String? = null,
    var customModel: String = "openrouter/free",
    private val fallback: ObjectDetectionProvider = MlKitImageLabelingProvider()
) : ObjectDetectionProvider {

    override suspend fun detect(imageBytes: ByteArray): DetectionResult = withContext(Dispatchers.IO) {
        val startedAt = System.nanoTime()

        // 1. Tenta Google Gemini se a chave estiver configurada
        val geminiKey = geminiApiKey?.trim()
        if (!geminiKey.isNullOrBlank()) {
            val result = callGemini(imageBytes, geminiKey, startedAt)
            if (result != null && result.detections.isNotEmpty()) {
                return@withContext result
            }
        }

        // 2. Tenta OpenRouter com pool de modelos gratuitos se a chave estiver configurada
        val openRouterKey = openRouterApiKey?.trim()
        if (!openRouterKey.isNullOrBlank()) {
            val candidateModels = listOfNotNull(
                customModel.takeIf { it.isNotBlank() },
                "openrouter/free",
                "dots-studio/dots-3-note-preview:free",
                "nex-agi/nex-n2.5-mini:free",
                "nex-agi/nex-n2.5-pro:free"
            ).distinct()

            for (model in candidateModels) {
                val result = callOpenRouter(imageBytes, openRouterKey, model, startedAt)
                if (result != null && result.detections.isNotEmpty()) {
                    return@withContext result
                }
            }
        }

        // 3. Fallback on-device transparente
        fallback.detect(imageBytes)
    }

    private fun callGemini(imageBytes: ByteArray, apiKey: String, startedAt: Long): DetectionResult? = runCatching {
        val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
        val endpoint = "https://generativelanguage.googleapis.com/v1/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val url = URL(endpoint)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }

        val prompt = "Analyze this food/dish image. Identify the main dish (e.g. 'Shawarma', 'Wrap de frango', 'Frango grelhado') and its visible components/ingredients. " +
            "Return ONLY a raw JSON array of objects with 'label' (name in Portuguese or English) and 'confidence' (number between 0.0 and 1.0). " +
            "Example: [{\"label\": \"Shawarma de frango\", \"confidence\": 0.98}, {\"label\": \"frango\", \"confidence\": 0.95}, {\"label\": \"salada\", \"confidence\": 0.90}]. Do not wrap in markdown."

        val payload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                        put(JSONObject().apply {
                            put("inline_data", JSONObject().apply {
                                put("mime_type", "image/jpeg")
                                put("data", base64Image)
                            })
                        })
                    })
                })
            })
        }

        connection.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            connection.disconnect()
            return null
        }

        val responseText = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()

        val json = JSONObject(responseText)
        val candidates = json.optJSONArray("candidates") ?: return null
        val firstCandidate = candidates.optJSONObject(0) ?: return null
        val content = firstCandidate.optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        val rawText = parts.optJSONObject(0)?.optString("text")?.trim() ?: return null

        val cleaned = rawText.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val detections = parseDetections(cleaned)

        DetectionResult(
            detections = detections,
            meta = InferenceMeta(
                providerId = "gemini_3.5_flash_cloud",
                version = "gemini-3.5-flash",
                runtime = "google-ai-cloud",
                latencyMs = (System.nanoTime() - startedAt) / 1_000_000
            )
        )
    }.getOrNull()

    private fun callOpenRouter(imageBytes: ByteArray, apiKey: String, model: String, startedAt: Long): DetectionResult? = runCatching {
        val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
        val url = URL("https://openrouter.ai/api/v1/chat/completions")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer $apiKey")
            setRequestProperty("HTTP-Referer", "https://eatcontrol.com.br")
            setRequestProperty("X-Title", "EatControl AI")
        }

        val prompt = "Identifique o prato principal (ex: 'Shawarma de frango', 'Wrap', etc.) e seus componentes visíveis. Retorne APENAS um array JSON de objetos com 'label' e 'confidence': [{\"label\": \"Shawarma de frango\", \"confidence\": 0.98}, {\"label\": \"frango\", \"confidence\": 0.95}]. Sem markdown."

        val payload = JSONObject().apply {
            put("model", model)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", JSONArray().apply {
                        put(JSONObject().apply {
                            put("type", "text")
                            put("text", prompt)
                        })
                        put(JSONObject().apply {
                            put("type", "image_url")
                            put("image_url", JSONObject().apply {
                                put("url", "data:image/jpeg;base64,$base64Image")
                            })
                        })
                    })
                })
            })
        }

        connection.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            connection.disconnect()
            return null
        }

        val responseText = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()

        val json = JSONObject(responseText)
        val choices = json.optJSONArray("choices") ?: return null
        val message = choices.optJSONObject(0)?.optJSONObject("message") ?: return null
        val contentText = message.optString("content").trim()

        val cleaned = contentText.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val detections = parseDetections(cleaned)

        DetectionResult(
            detections = detections,
            meta = InferenceMeta(
                providerId = "openrouter_vision_cloud",
                version = model,
                runtime = "openrouter-cloud",
                latencyMs = (System.nanoTime() - startedAt) / 1_000_000
            )
        )
    }.getOrNull()

    private fun parseDetections(rawJson: String): List<Detection> {
        // Tenta primeiro interpretar como array JSON estrito
        val jsonResult = runCatching {
            val array = JSONArray(rawJson)
            val list = mutableListOf<Detection>()
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val label = obj.optString("label").trim()
                val conf = obj.optDouble("confidence", 0.9).toFloat()
                if (label.isNotBlank()) {
                    list.add(Detection(label = label, confidence = conf))
                }
            }
            list
        }.getOrNull()

        if (!jsonResult.isNullOrEmpty()) return jsonResult

        // Fallback tolerante para modelos gratuitos que respondem em texto livre
        return rawJson.lineSequence()
            .map { it.trim().trim('-', '*', '•') }
            .filter { it.isNotBlank() && !it.startsWith("{") && !it.startsWith("[") }
            .take(3)
            .map { Detection(label = it, confidence = 0.90f) }
            .toList()
    }

    companion object {
        private const val TIMEOUT_MS = 8_000
    }
}
