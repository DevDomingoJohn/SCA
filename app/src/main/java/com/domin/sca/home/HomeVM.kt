package com.domin.sca.home

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel

class HomeVM(
    private val connectivityManager: ConnectivityManager
): ViewModel() {

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

}