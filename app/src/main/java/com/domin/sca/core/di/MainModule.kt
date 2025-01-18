package com.domin.sca.core.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager

interface MainModule {
    val wifiManager: WifiManager
    val connectivityManager: ConnectivityManager
}

class MainModuleImpl(
    private val context: Context
): MainModule {
    override val wifiManager: WifiManager by lazy {
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
    override val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}