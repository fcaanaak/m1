package com.example.cpen321application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.graphics.toColorInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CPEN321ApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column {
                        Greeting(
                            apiBaseUrl = BuildConfig.API_BASE_URL,
                            modifier = Modifier.padding(innerPadding)
                        )

                        ColorGrid()
                    }

                }
            }
        }
    }
}

fun urlConcatenate(baseURL: String, endpoint: String) = "${baseURL.trimEnd('/')}/${endpoint}"


// TODO: Add a websocket client here that will connect to the server's websocket endpoint
// TODO: and update the grid with color values based on the JSON data each time an update is received
// TODO: from the websocket server
@Composable
fun ColorGrid() {
    var grid by remember { mutableStateOf(Array(16){Array(16){"#ffffff"} }) }

    grid[8][8] = "#ff00ff"
    grid[7][7] = "#ff00ff"
    grid[7][8] = "#ff00ff"
    grid[8][7] = "#ff00ff"

    Column {
        for (colorRow in grid) {
            Row{
                for (item in colorRow){
                    Box(Modifier.background(Color(item.toColorInt())).padding(10.dp))
                }
            }
        }

    }

}

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