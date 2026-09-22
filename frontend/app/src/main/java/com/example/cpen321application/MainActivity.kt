package com.example.cpen321application

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()

            setContent {
                CPEN321ApplicationTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Column (
                            Modifier.padding(innerPadding).fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NavButton(LocalContext.current,
                                PixelGridActivity::class.java,
                                "Live Updates",
                                "updates".toUri()
                            )
                            NavButton(LocalContext.current,
                                LoginActivity::class.java,
                                "Login + Server",
                                "login".toUri()
                            )
                            NavButton(LocalContext.current,
                                TimerActivity::class.java,
                                "Timer + Surprise",
                                "surprise".toUri()
                                )
                        }
                    }
                }
            }
        }
}

@Composable
fun <T> NavButton(context: Context, destination: Class<T>, text: String, uri: Uri) {
    Button(onClick = {
        context.startActivity(Intent(Intent.ACTION_SEND, uri,
            context, destination))
    }, modifier = Modifier.padding(16.dp)
    ) {
        Text(text)
    }
}





