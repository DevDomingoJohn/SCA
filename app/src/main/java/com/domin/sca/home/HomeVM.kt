package com.domin.sca.home

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domin.sca.core.clientSocket
import com.domin.sca.core.network.ClientSocket
import com.domin.sca.core.network.ServerSocket
import com.domin.sca.core.serverSocket
import com.domin.sca.server.ServerScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeVM(
    private val wifiManager: WifiManager,
    private val connectivityManager: ConnectivityManager
): ViewModel() {

//    private val _state = MutableStateFlow(State())
//    val state = _state.asStateFlow()

    init {
        val network: Network? = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        networkCapabilities?.let {
            if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val linkProperties = connectivityManager.getLinkProperties(network)
                val result = linkProperties?.linkAddresses?.firstOrNull { linkAddress ->
                    linkAddress.address.hostAddress?.contains(".") == true
                }?.address?.hostAddress!!
                Log.i("HomeVM",result)
            }
        }
    }

    fun startServer(port: Int) {
        viewModelScope.launch {
            serverSocket = ServerSocket(port)
            serverSocket.start()
        }
    }

    fun connectToServer(ip: String, port: Int) {
        viewModelScope.launch {
            clientSocket = ClientSocket(ip,port)
            clientSocket.connect()
        }
    }
}