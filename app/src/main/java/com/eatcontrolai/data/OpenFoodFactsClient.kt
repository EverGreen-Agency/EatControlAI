package com.eatcontrolai.data

import com.eatcontrolai.domain.barcode.OpenFoodFactsMapper
import com.eatcontrolai.domain.barcode.ProductBarcodeData
import com.eatcontrolai.domain.barcode.ProductDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Consulta o Open Food Facts por código de barras.
 *
 * Sem biblioteca de rede: é uma chamada GET a um endpoint público, e `HttpURLConnection` resolve.
 * Retrofit e OkHttp acrescentariam alguns megabytes ao APK e uma dependência a manter, para
 * economizar as vinte linhas abaixo.
 *
 * O que sai daqui é só o código de barras — nenhum dado do usuário, nenhuma foto, nenhum perfil.
 * Essa fronteira é a razão de a promessa de privacidade continuar verdadeira mesmo com esta consulta
 * existindo: a rede sabe qual produto foi escaneado, nunca quem escaneou nem por quê.
 */
class OpenFoodFactsClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val userAgent: String = DEFAULT_USER_AGENT
) : ProductDataSource {

    override suspend fun lookup(ean: String): ProductBarcodeData? = withContext(Dispatchers.IO) {
        val url = URL("$baseUrl/api/v2/product/$ean.json?fields=$FIELDS")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
            // A política da base pede identificação do cliente; anônima, sem dado de usuário.
            setRequestProperty("User-Agent", userAgent)
            setRequestProperty("Accept", "application/json")
        }

        try {
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return@withContext null
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            OpenFoodFactsMapper.map(body, ean)
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val DEFAULT_BASE_URL = "https://world.openfoodfacts.org"

        /** Sem versão dinâmica de propósito: o cabeçalho não deve identificar aparelho nem build. */
        const val DEFAULT_USER_AGENT = "EatControl/1.0 (contato via eatcontrol.app)"

        const val TIMEOUT_MS = 6_000

        /** Só os campos usados. Pedir o registro inteiro seria trafegar centenas de KB por consulta. */
        const val FIELDS = "product_name,product_name_pt,brands,ingredients_text," +
            "ingredients_text_pt,allergens_tags,traces_tags,nutriments"
    }
}
