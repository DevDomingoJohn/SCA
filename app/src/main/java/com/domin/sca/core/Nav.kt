package com.domin.sca.core

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class Server(val localIp: String, val port: Int)

@Serializable
data class Client(val localIp: String, val ip: String, val port: Int)