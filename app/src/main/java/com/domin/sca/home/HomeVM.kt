package com.domin.sca.home

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeVM(
    private val connectivityManager: ConnectivityManager
): ViewModel() {

    private val _state = MutableStateFlow(UIState())
    val state = _state.asStateFlow()

    fun getLocalIp(): String {
        var result = ""
        val network: Network? = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        networkCapabilities?.let {
            if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val linkProperties = connectivityManager.getLinkProperties(network)
                result = linkProperties?.linkAddresses?.firstOrNull { linkAddress ->
                    linkAddress.address.hostAddress?.contains(".") == true
                }?.address?.hostAddress!!
            }
        }

        return result
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