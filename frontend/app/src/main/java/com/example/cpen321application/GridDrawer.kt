package com.example.cpen321application

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class GridDrawer : ViewModel() {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null


    // Breaks encapsulation but I really dont care atp
    val _grid = SnapshotStateList<SnapshotStateList<String>>()

    init {

        lateinit var tempList: SnapshotStateList<String>

        for (i in 1..16) {
            tempList = mutableStateListOf()
            for (j in 1..16) {
                tempList.add("#ffffff")
            }
            _grid.add(tempList)
        }
    }

    fun connect() {
        val request = Request.Builder()
            .url(BuildConfig.API_WS_URL)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onMessage(webSocket: WebSocket, text: String) {
                viewModelScope.launch {
                    val jObj = JSONObject(text)
                    _grid[jObj.getInt("x")][jObj.getInt("y")] = jObj.getString("color")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                t.printStackTrace()
            }
        })
    }

    override fun onCleared() {
        webSocket?.close(1000, "Disconnect")
        client.dispatcher.executorService.shutdown()
        super.onCleared()
    }


}