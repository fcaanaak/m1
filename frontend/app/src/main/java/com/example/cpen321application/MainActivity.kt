package com.example.cpen321application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CPEN321ApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        apiBaseUrl = BuildConfig.API_BASE_URL,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

fun urlConcatenate(baseURL: String, endpoint: String) = "${baseURL.trimEnd('/')}/${endpoint}"

@Composable
fun Greeting(apiBaseUrl: String, modifier: Modifier = Modifier) {
    var statusText by remember { mutableStateOf("Checking backend at $apiBaseUrl/health...") }
    var ipText by remember {   mutableStateOf("Checking IP at $apiBaseUrl/ip...") }
    var timeText by remember {mutableStateOf("Checking Time")}

    LaunchedEffect(apiBaseUrl) {
        statusText = fetchGet(urlConcatenate(apiBaseUrl,"health"))
        ipText = fetchGet(urlConcatenate(apiBaseUrl,"ip"))
        timeText = fetchGet(urlConcatenate(apiBaseUrl,"time"))
    }

    Column {
        Text(
            text = statusText,
            modifier = modifier
        )

        Text(
            text = ipText,
            modifier = modifier
        )

        Text(
            text = timeText,
            modifier = modifier
        )
    }
}

private suspend fun fetchGet(url: String, timeoutMillis: Int = 5_000): String = withContext(Dispatchers.IO) {
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
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
                "Backend error ($url): HTTP $code${errorBody?.let { " — $it" } ?: ""}"
            }
        }
    } catch (e: Exception) {
        "Backend unreachable ($url): ${e.message ?: e.javaClass.simpleName}"
    }
}