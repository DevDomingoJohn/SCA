package com.domin.sca.server

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
fun ServerScreen(
    localIp: String,
    port: Int,
    onBackPressed: () -> Unit
) {
    val message = remember { mutableStateOf("") }
    val vm = viewModel<ServerVM>()
    val logs by vm.logs.collectAsState()

    LaunchedEffect(key1 = true) {
        vm.startServer(port)
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .windowInsetsPadding(
                WindowInsets(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
            )
    ) {
        Text(text = "Your Local IP: $localIp")
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(logs) { log ->
                Text(text = log)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                value = message.value,
                onValueChange = { message.value = it },
                placeholder = { Text(text = "Message") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    vm.message(message.value)
                    message.value = ""
                }
            ) {
                Text(text = "Send")
            }
        }
    }

    BackHandler {
        vm.stopServer()
        onBackPressed()
    }
}