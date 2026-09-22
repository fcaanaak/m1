package com.example.cpen321application

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme
import java.util.Timer
import java.util.TimerTask
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

class TimerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CPEN321ApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Sample(Modifier.padding(innerPadding), LocalContext.current)
                }
            }
        }
    }
}


fun secondsToMillis(seconds: Long): Long = seconds * 1000


@Composable
fun Sample(modifier: Modifier, context: Context){

    var minutes by remember {mutableStateOf("00")}
    var seconds by remember { mutableStateOf("00") }
    var inputEnabled by remember {mutableStateOf(true)}
    var timerStatusText by remember { mutableStateOf("Start Timer") }

    val t = Timer()

    Column(modifier.padding().fillMaxSize()) {
        Text("Timer", modifier = modifier)

        Row (modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Text("Minutes", modifier = Modifier.padding(16.dp))
            DoubleDigitIntField(modifier, minutes, inputEnabled) { minutes = it.take(2) }
        }

        Row (modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Text("Seconds", modifier = Modifier.padding(16.dp))
            DoubleDigitIntField(modifier, seconds, inputEnabled) {seconds = it.take(2)}
        }

        Button(
            onClick = {
                if (minutes.isNotEmpty() && seconds.isNotEmpty()) {
                    t.schedule(object: TimerTask() {
                        override fun run() {
                            context.startActivity(
                                Intent(context, SurpriseActivity::class.java)
                            )
                        }
                    }, secondsToMillis(minutes.toLong()*60) + secondsToMillis(seconds.toLong()))
                    inputEnabled = false
                    timerStatusText = "Timer Running"
                }
            },
            enabled = inputEnabled
        ) {Text(timerStatusText)}
    }
}

@Composable
fun DoubleDigitIntField(modifier: Modifier, value: String, status: Boolean, onValueChange: (String)->Unit) {

    TextField(
        value = value,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
        modifier = modifier,
        onValueChange = onValueChange,
        maxLines = 1,
        enabled = status
    )
}
