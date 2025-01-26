package com.domin.sca.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.domin.sca.core.MyApp
import com.domin.sca.core.utils.ViewModelFactoryHelper

@Composable
fun HomeScreen(
    navigateServer: (String, Int) -> Unit,
    navigateClient: (String, String, Int) -> Unit
) {

    val vm = viewModel<HomeVM>(
        factory = ViewModelFactoryHelper(HomeVM(MyApp.mainModule.connectivityManager))
    )
    val localIp = vm.getLocalIp()

    val serverPort = remember { mutableStateOf("") }
    val port = remember { mutableStateOf("") }
    val ip = remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Server-Client",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(50.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = Color.Red)
                .padding(10.dp)
        ) {
            TextField(
                value = serverPort.value,
                onValueChange = {serverPort.value = it},
                placeholder = { Text(text = "Port") },
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            Button(
                onClick = {
                    navigateServer(localIp,serverPort.value.toInt())
                }
            ) {
                Text(text = "Start Server")
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = Color.Blue)
                .padding(10.dp)
        ) {
            TextField(
                value = ip.value,
                onValueChange = {ip.value = it},
                placeholder = { Text(text = "Server IP") },
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            TextField(
                value = port.value,
                onValueChange = {port.value = it},
                placeholder = { Text(text = "Server Port") },
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            Button(
                onClick = {
                    navigateClient(localIp,ip.value,port.value.toInt())
                }
            ) {
                Text(text = "Connect To Server")
            }
        }
    }
}