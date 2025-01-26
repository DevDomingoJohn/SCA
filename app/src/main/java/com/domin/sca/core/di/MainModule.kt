package com.domin.sca.core.di

import android.content.Context
import android.net.ConnectivityManager

interface MainModule {
    val connectivityManager: ConnectivityManager
}

class MainModuleImpl(
    private val context: Context
): MainModule {
    override val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}