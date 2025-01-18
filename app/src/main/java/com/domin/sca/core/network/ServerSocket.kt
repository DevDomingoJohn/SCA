package com.domin.sca.core.network

import android.util.Log
import com.domin.sca.server.ServerScreen
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class ServerSocket(
    private val port: Int
) {
    fun start() {
        Thread {
            try {
                val serverSocket = ServerSocket(port)

                while(true) {
                    val socket = serverSocket.accept()
                    if (socket.isConnected)
                        Log.i("Server Socket","Client Connected: ${socket.inetAddress.hostAddress}")

                    Thread {
                        handleClient(socket)
                    }.start()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun handleClient(client: Socket) {
        try {
            val inputStream = client.getInputStream()
            val outputStream = client.getOutputStream()

            val buffer = ByteArray(1024)
            val bytesRead = inputStream.read(buffer)
            val message = String(buffer,0,bytesRead)

            val server = "Welcome To Soul Society!"
            outputStream.write(server.toByteArray())
            outputStream.flush()

            client.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}