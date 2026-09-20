package com.example.cpen321application

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClientServerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            CPEN321ApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(BuildConfig.API_BASE_URL, intent.extras!!,modifier = Modifier.padding(innerPadding),
                        LocalContext.current
                    )
                }
            }
        }
    }
}

fun urlConcatenate(baseURL: String, endpoint: String) = "${baseURL.trimEnd('/')}/${endpoint}"

@Composable
fun Greeting(apiBaseUrl: String, googleData: Bundle, modifier: Modifier = Modifier, context: Context) {
    var serverIp by remember { mutableStateOf("Checking IP at $apiBaseUrl/ip...") }
    var serverTime by remember { mutableStateOf("Checking Time") }
    var serverFullName by remember { mutableStateOf("Getting server full name") }

    var clientTimeText by remember { mutableStateOf("Getting client time")}
    var clientIPAddress: String? by remember { mutableStateOf("") }
    var clientFirstName: String? by remember { mutableStateOf("Getting client first name") }
    var clientLastName: String? by remember { mutableStateOf("Getting client last name") }


    LaunchedEffect(apiBaseUrl) {
        serverIp = JSONObject(fetchGet(urlConcatenate(apiBaseUrl, "ip"))).getString("serverIP")
        serverTime = JSONObject(fetchGet(urlConcatenate(apiBaseUrl, "time"))).getString("time")

        val serverNameJson: JSONObject = JSONObject(fetchGet(urlConcatenate(apiBaseUrl,"name"))).getJSONObject("name")
        serverFullName = "${serverNameJson.getString("firstName")} ${serverNameJson.getString("lastName")}"

        val gmtOffset = SimpleDateFormat("ZZZZ", Locale.getDefault()).format(System.currentTimeMillis())

        clientTimeText = "${SimpleDateFormat("HH:mm:ss").format(Date())} $gmtOffset"

        clientFirstName = googleData.getString("firstName") ?: "No first name"
        clientLastName = googleData.getString("lastName") ?: "No last name"

        withContext(Dispatchers.IO) {
            clientIPAddress = fetchGet("https://api.ipify.org/") // APi to get public IP
        }


    }

    Column {

        Text(
            text = "Server IP Address: $serverIp",
            modifier = modifier
        )

        Text (
            text = if (clientIPAddress != null) "Client Public IP Address: $clientIPAddress" else "IP Address fetching error",
            modifier = modifier
        )

        Text(
            text = "Server Local Time: $serverTime",
            modifier = modifier
        )

        Text (
            text = "Client Local Time: $clientTimeText",
            modifier = modifier
        )

        Text (
            text = "Developer Name (from server): $serverFullName",
            modifier = modifier
        )

        Text (
            text = "Client Full Name: $clientFirstName $clientLastName",
            modifier = modifier
        )

    }
}

private suspend fun fetchGet(url: String, timeoutMillis: Int = 5_000): String =
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