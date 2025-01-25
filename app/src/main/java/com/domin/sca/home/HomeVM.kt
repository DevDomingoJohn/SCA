package com.domin.sca.home

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domin.sca.core.clientSocket
import com.domin.sca.core.localIp
import com.domin.sca.core.network.ClientSocket
import com.domin.sca.core.network.ServerSocket
import com.domin.sca.core.serverSocket
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeVM(
    private val wifiManager: WifiManager,
    private val connectivityManager: ConnectivityManager
): ViewModel() {

    init {
        val network: Network? = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        networkCapabilities?.let {
            if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val linkProperties = connectivityManager.getLinkProperties(network)
                localIp = linkProperties?.linkAddresses?.firstOrNull { linkAddress ->
                    linkAddress.address.hostAddress?.contains(".") == true
                }?.address?.hostAddress!!
            }
        }
    }
}