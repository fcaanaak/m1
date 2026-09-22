package com.example.cpen321application

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SurpriseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CPEN321ApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    XKCDComic(
                        Modifier.padding(innerPadding),
                        LocalContext.current
                    )
                }
            }
        }
    }
}

@Composable
fun XKCDComic(modifier: Modifier, context: Context) {

    val placeHolderImageURL = "https://i.pinimg.com/originals/31/de/8d/31de8dd17d15fd0d0de29d00afa8c3ac.jpg"

    val placeHolderJSONObject = JSONObject("""
        {
            "safe_title": "Getting title",
            "img": "$placeHolderImageURL"  
        }
    """.trimIndent())

    var comicData by remember { mutableStateOf(placeHolderJSONObject) }

    var comicNumber by remember { mutableIntStateOf((1000..3000).random()) }

    LaunchedEffect(comicNumber) {

        comicData = JSONObject(Utils.fetchGet("https://xkcd.com/$comicNumber/info.0.json"))
    }

    Column(modifier = modifier.padding().fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = comicData.getString("safe_title"),
            modifier = modifier
        )

        Button(
            onClick = {comicNumber = (1000..3000).random()},
            modifier = modifier
        ) { Text("Fetch new comic")}

        AsyncImage(
            model = ImageRequest
                .Builder(context)
                .data(comicData.getString("img"))
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = modifier.padding().fillMaxSize()
        )
    }


}



