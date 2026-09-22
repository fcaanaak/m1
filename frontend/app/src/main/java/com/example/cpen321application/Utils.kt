package com.example.cpen321application

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class Utils {

    companion object {
        suspend fun fetchGet(url: String, timeoutMillis: Int = 5_000): String =
            withContext(Dispatchers.IO) {
                try {
                    val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = timeoutMillis
                        readTimeout = timeoutMillis
                    }

                    when (val code = connection.responseCode) {
                        HttpURLConnection.HTTP_OK -> {
                            val body = connection.inputStream.bufferedReader().use { it.readText() }
                            body
                        }

                        else -> {
                            val errorBody =
                                connection.errorStream?.bufferedReader()?.use { it.readText() }
                            "Backend error ($url): HTTP $code${errorBody?.let { " — $it" } ?: ""}"
                        }
                    }
                } catch (e: Exception) {
                    "Backend unreachable ($url): ${e.message ?: e.javaClass.simpleName}"
                }
            }
    }


}