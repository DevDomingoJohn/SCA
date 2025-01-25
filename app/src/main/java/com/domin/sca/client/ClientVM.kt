package com.domin.sca.client

import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domin.sca.core.clientSocket
import com.domin.sca.core.network.ClientSocket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientVM(
    private val wifiManager: WifiManager,
    private val connectivityManager: ConnectivityManager
): ViewModel() {
    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs = _logs.asStateFlow()

    fun addLog(log: String) {
        viewModelScope.launch {
            _logs.update { it + log }
        }
    }

    fun message(text: String) {
        viewModelScope.launch {
            clientSocket.message(text)
            addLog(text)
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

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            clientSocket.disconnect()
        }
    }
}