package com.example.cpen321application

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    var secondsLeft by remember { mutableLongStateOf(0) }

    var canceled by remember {mutableStateOf(false)}

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

        // Source - https://stackoverflow.com/a/69151539
        // Posted by Phil Dukhov, modified by community. See post 'Timeline' for change history
        // Retrieved 2026-09-21, License - CC BY-SA 4.0
        val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
        var backPressHandled by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        BackHandler(enabled = !backPressHandled) {
            canceled = true
            backPressHandled = true
            coroutineScope.launch {
                awaitFrame()
                onBackPressedDispatcher?.onBackPressed()
                backPressHandled = false
            }
        }

        Button(
            onClick = {
                if (minutes.isNotEmpty() && seconds.isNotEmpty()) {

                    secondsLeft = minutes.toLong()*60 +seconds.toLong()

                    t.schedule(object: TimerTask() {
                        override fun run () {

                            timerStatusText = "Seconds left: $secondsLeft"

                            if (secondsLeft <= 0L) {

                                inputEnabled = true
                                timerStatusText = "Start Timer"

                                t.cancel()

                                if (!canceled) {
                                    context.startActivity(
                                        Intent(context, SurpriseActivity::class.java)
                                    )
                                } else {
                                    canceled = false
                                }

                            }

                            secondsLeft -= 1
                        }
                    }, 0, 1000)

                    inputEnabled = false

                } else {
                    Toast.makeText(context, "No empty fields allowed", Toast.LENGTH_SHORT).show()
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
