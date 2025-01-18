package com.domin.sca.server

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.domin.sca.core.MyApp
import com.domin.sca.core.utils.ViewModelFactoryHelper

@Composable
fun ServerScreen() {
    val vm = viewModel<ServerVM>(
        factory = ViewModelFactoryHelper(
            ServerVM(
                MyApp.mainModule.wifiManager,
                MyApp.mainModule.connectivityManager
            )
        )
    )


}