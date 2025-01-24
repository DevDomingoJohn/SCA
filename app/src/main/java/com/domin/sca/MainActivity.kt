package com.domin.sca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.domin.sca.client.ClientScreen
import com.domin.sca.core.Client
import com.domin.sca.core.Home
import com.domin.sca.core.MyApp
import com.domin.sca.core.Server
import com.domin.sca.core.utils.ViewModelFactoryHelper
import com.domin.sca.home.HomeScreen
import com.domin.sca.home.HomeVM
import com.domin.sca.server.ServerScreen
import com.domin.sca.ui.theme.SCATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SCATheme {
                val viewModel = viewModel<HomeVM>(
                    factory = ViewModelFactoryHelper(
                        HomeVM(MyApp.mainModule.wifiManager, MyApp.mainModule.connectivityManager)
                    )
                )

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Home
                ) {
                    composable<Home> {
                        HomeScreen(
                            navigateServer = { navController.navigate(Server) },
                            navigateClient = { navController.navigate(Client) },
                            viewModel = viewModel
                        )
                    }
                    composable<Server> {
                        //val args = it.toRoute<Server>()
                        ServerScreen()
                    }
                    composable<Client> {
                        ClientScreen()
                    }
                }
            }
        }
    }
}