package com.domin.sca.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.net.NetworkInterface

class HomeVM: ViewModel() {
    private val _state = MutableStateFlow(UIState())
    val state = _state.asStateFlow()

    fun getLocalIp(): String? {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val addresses = networkInterface.inetAddresses

            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()
                if (!address.isLoopbackAddress && address.hostAddress?.contains(".") == true) {
                    return address.hostAddress
                }
            }
        }

        return null
    }

    fun updateServerPort(value: String) {
        _state.update { it.copy(
            serverPort = value
        ) }
    }

    fun updateClientIp(value: String) {
        _state.update { it.copy(
            ip = value
        ) }
    }

    fun updateClientPort(value: String) {
        _state.update { it.copy(
            port = value
        ) }
    }

    fun validateServerField(): Boolean {
        if (state.value.serverPort.isBlank()){
            _state.update { it.copy(errorMessage = "Port Required") }
            return false
        } else if (!state.value.serverPort.matches(Regex("\\d+"))) {
            _state.update { it.copy(errorMessage = "Invalid Port") }
            return false
        } else {
            return true
        }
    }

    fun validateClientFields(): Boolean {
        val ipv4Regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$"
        if (state.value.ip.isBlank() || state.value.port.isBlank()) {
            _state.update { it.copy(errorMessage = "Empty Field/s") }
            return false
        } else if (!state.value.ip.matches(Regex(ipv4Regex))) {
            _state.update { it.copy(errorMessage = "Invalid IP") }
            return false
        } else if (!state.value.port.matches(Regex("\\d+"))) {
            _state.update { it.copy(errorMessage = "Invalid Port") }
            return false
        } else {
            return true
        }
    }
}