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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme

class PixelGridActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CPEN321ApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ColorGrid(GridDrawer(), Modifier.padding(innerPadding))
                }
            }
        }
    }
}
@Composable
fun ColorGridContent(
    currGrid: SnapshotStateList<SnapshotStateList<String>>
) {

    Box (contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        Row{

            for (colorRow in currGrid) {
                Column {
                    for (item in colorRow) {
                        Box(Modifier.background(Color(item.toColorInt())).padding(10.dp))
                    }
                }
            }

        }
    }

}

@Composable
fun ColorGrid(viewModel: GridDrawer, modifier: Modifier) {
    val grid by remember { derivedStateOf { viewModel._grid } }

    viewModel.connect()

    ColorGridContent(grid)

}
