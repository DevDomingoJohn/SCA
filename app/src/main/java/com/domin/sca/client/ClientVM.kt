package com.domin.sca.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domin.sca.core.network.ClientSocket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientVM: ViewModel() {
    private lateinit var clientSocket: ClientSocket
    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs = _logs.asStateFlow()

    private fun addLog(log: String) {
        viewModelScope.launch {
            _logs.update { it + log }
        }
    }

    fun message(text: String) {
        viewModelScope.launch {
            clientSocket.message(text)
            addLog("Me: $text")
        }
    }

    fun connectToServer(ip: String, port: Int) {
        viewModelScope.launch {
            clientSocket = ClientSocket(ip,port) {
                addLog(it)
            }
            clientSocket.connect()
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            clientSocket.disconnect()
        }
    }
}