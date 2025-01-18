package com.domin.sca.core.network

import java.io.IOException
import java.net.Socket

class ClientSocket(
    private val ip: String,
    private val port: Int
) {
    fun connect() {
        Thread {
            try {
                val socket = Socket(ip,port)

                val outputStream = socket.getOutputStream()
                val client = "Hello I'm ${socket.inetAddress.hostAddress}"
                outputStream.write(client.toByteArray())
                outputStream.flush()

                val inputStream = socket.getInputStream()
                val buffer = ByteArray(1024)
                val bytesRead = inputStream.read(buffer)
                val message = String(buffer,0,bytesRead)

                socket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }
}