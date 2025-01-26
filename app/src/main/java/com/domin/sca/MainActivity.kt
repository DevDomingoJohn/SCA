package com.domin.sca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.domin.sca.client.ClientScreen
import com.domin.sca.core.Client
import com.domin.sca.core.Home
import com.domin.sca.core.Server
import com.domin.sca.home.HomeScreen
import com.domin.sca.server.ServerScreen
import com.domin.sca.ui.theme.SCATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SCATheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Home
                ) {
                    composable<Home> {
                        HomeScreen(
                            navigateServer = { localIp, port -> navController.navigate(Server(localIp, port)) },
                            navigateClient = { localIp, ip, port -> navController.navigate(Client(localIp, ip, port)) }
                        )
                    }
                    composable<Server> {
                        val args = it.toRoute<Server>()
                        ServerScreen(args.localIp,args.port) {
                            navController.popBackStack()
                        }
                    }
                    composable<Client> {
                        val args = it.toRoute<Client>()
                        ClientScreen(args.localIp,args.ip,args.port) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}