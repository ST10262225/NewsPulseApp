package com.example.newspulse.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

object Translator {

    suspend fun translateText(text: String, targetLang: String): String = withContext(Dispatchers.IO) {
        try {
            // Encode text to avoid breaking URLs with spaces or special characters
            val encodedText = URLEncoder.encode(text, "UTF-8")

            // Google Translate (unofficial endpoint)
            val url = URL("https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=$targetLang&dt=t&q=$encodedText")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val response = connection.inputStream.bufferedReader().use { it.readText() }

            // Parse translation result — the first element in the nested array
            val jsonArray = JSONArray(response)
            val translatedText = jsonArray
                .getJSONArray(0)
                .getJSONArray(0)
                .getString(0)

            connection.disconnect()
            translatedText
        } catch (e: Exception) {
            e.printStackTrace()
            text // fallback if something fails
        }
    }
}
