package com.domin.sca.client

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.domin.sca.core.MyApp
import com.domin.sca.core.utils.ViewModelFactoryHelper

@Composable
fun ClientScreen() {
    val vm = viewModel<ClientVM>(
        factory = ViewModelFactoryHelper(
            ClientVM(
                MyApp.mainModule.wifiManager,
                MyApp.mainModule.connectivityManager
            )
        )
    )


}