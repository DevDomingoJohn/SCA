package com.domin.sca.server

import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domin.sca.core.localIp
import com.domin.sca.core.network.ServerSocket
import com.domin.sca.core.serverSocket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServerVM(
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
            serverSocket.message(text)
            addLog(text)
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

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            serverSocket.stop()
        }
    }
}