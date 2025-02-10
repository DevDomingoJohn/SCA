package com.domin.sca.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    navigateServer: (String, Int) -> Unit,
    navigateClient: (String, String, Int) -> Unit
) {
    val vm = viewModel<HomeVM>()
    val state by vm.state.collectAsState()
    val localIp = vm.getLocalIp()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {

        state.errorMessage?.let {
            Text(it, color = Color.Red)
        }

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
                value = state.serverPort,
                onValueChange = {vm.updateServerPort(it)},
                placeholder = { Text(text = "Port") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            Button(
                onClick = {
                    if (vm.validateServerField() && localIp != null) {
                        navigateServer(localIp,state.serverPort.toInt())
                    }
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
                value = state.ip,
                onValueChange = { vm.updateClientIp(it) },
                placeholder = { Text(text = "Server IP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            TextField(
                value = state.port,
                onValueChange = { vm.updateClientPort(it) },
                placeholder = { Text(text = "Server Port") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            Button(
                onClick = {
                    if (vm.validateClientFields() && localIp != null) {
                        navigateClient(localIp,state.ip,state.port.toInt())
                    }
                }
            ) {
                Text(text = "Connect To Server")
            }
        }
    }
}