package com.domin.sca.client

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ClientScreen(
    localIp: String,
    ip: String,
    port: Int,
    onBackPressed: () -> Unit
) {
    val vm = viewModel<ClientVM>()
    val message = remember { mutableStateOf("") }
    val logs by vm.logs.collectAsState()

    LaunchedEffect(key1 = true) {
        vm.connectToServer(ip,port)
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp, start = 10.dp, end = 10.dp)
    ) {
        Text(text = "Your Local IP: $localIp")
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
        ) {
            items(logs) { log ->
                Text(text = log)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
        ) {
            TextField(
                value = message.value,
                onValueChange = { message.value = it },
                placeholder = { Text(text = "Message") },
                modifier = Modifier.weight(0.7f)
            )
            Button(
                onClick = {
                    vm.message(message.value)
                    message.value = ""
                },
                modifier = Modifier.weight(0.3f)
            ) {
                Text(text = "Send")
            }
        }
    }

    BackHandler {
        vm.disconnect()
        onBackPressed()
    }
}