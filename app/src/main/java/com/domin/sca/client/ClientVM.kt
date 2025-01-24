package com.domin.sca.client

import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domin.sca.core.clientSocket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientVM(
    private val wifiManager: WifiManager,
    private val connectivityManager: ConnectivityManager
): ViewModel() {
    private val _state = MutableStateFlow(ClientState())
    val state = _state.asStateFlow()

    fun message(text: String) {
        viewModelScope.launch {
            clientSocket.message(text)

            val list = state.value.logs.toMutableList()
            list.add(text)
            _state.update { it.copy(
                logs = list
            ) }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}