package com.domin.sca.core

import android.app.Application
import com.domin.sca.core.di.MainModuleImpl

class MyApp: Application() {
    companion object {
        lateinit var mainModule: MainModuleImpl
    }

    override fun onCreate() {
        super.onCreate()
        mainModule = MainModuleImpl(this)
    }
}