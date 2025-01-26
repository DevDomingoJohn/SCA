package com.domin.sca.server

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domin.sca.core.network.ServerSocket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServerVM: ViewModel() {
    private lateinit var serverSocket: ServerSocket
    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs = _logs.asStateFlow()

    private fun addLog(log: String) {
        viewModelScope.launch {
            _logs.update { it + log }
        }
    }

    fun message(text: String) {
        viewModelScope.launch {
            serverSocket.message(text)
            addLog("Me: $text")
        }
    }

    fun startServer(port: Int) {
        viewModelScope.launch {
            serverSocket = ServerSocket(port) {
                addLog(it)
            }
            serverSocket.start()
        }
    }

    fun stopServer() {
        viewModelScope.launch {
            serverSocket.stop()
        }
    }
}