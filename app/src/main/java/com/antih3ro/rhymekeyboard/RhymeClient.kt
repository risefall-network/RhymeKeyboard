package com.antih3ro.rhymekeyboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class RhymeClient {
    private val client = OkHttpClient()

    suspend fun fetchRhymes(word: String, max: Int = 12): List<String> = withContext(Dispatchers.IO) {
        if (word.isBlank()) return@withContext emptyList()
        val url = "https://api.datamuse.com/words?rel_rhy=${word.trim()}&max=$max"
        val req = Request.Builder().url(url).get().build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return@use emptyList()
            val body = resp.body.string()
            val arr = JSONArray(body)
            val out = mutableListOf<String>()
            for (i in 0 until arr.length()) {
                val w = arr.getJSONObject(i).optString("word")
                if (w.isNotBlank()) out.add(w)
            }
            out
        }
    }
}
