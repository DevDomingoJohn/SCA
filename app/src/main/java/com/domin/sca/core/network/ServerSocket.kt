package com.domin.sca.core.network

import java.io.IOException
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class ServerSocket(
    private val port: Int,
    val addLog: (String) -> Unit
) {
    private lateinit var serverSocket: ServerSocket
    private lateinit var outputStream: OutputStream
    private val isRunning = AtomicBoolean(true)
    private val clientSockets = mutableListOf<Socket>()

    fun start() {
        Thread {
            try {
                serverSocket = ServerSocket(port)
                addLog("Started Server on port: $port")

                while(isRunning.get()) {
                    val socket = serverSocket.accept()
                    if (socket.isConnected) {
                        addLog("${socket.inetAddress.hostAddress} Joined The Server")

                        outputStream = socket.getOutputStream()
                        val server = "Welcome To Soul Society!"
                        outputStream.write(server.toByteArray())
                        outputStream.flush()
                    }

                    clientSockets.add(socket)

                    Thread {
                        handleClient(socket)
                    }.start()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun handleClient(client: Socket) {
        try {
            val inputStream = client.getInputStream()

            while(isRunning.get()) {
                val buffer = ByteArray(1024)
                val bytesRead = inputStream.read(buffer)
                if (bytesRead == -1) break
                val message = String(buffer,0,bytesRead)
                addLog("${client.inetAddress.hostAddress}: $message")
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            client.close()
            clientSockets.remove(client)
            addLog("${client.inetAddress.hostAddress} Disconnected!")
        }
    }

    fun message(text: String) {
        Thread {
            try {
                outputStream.write(text.toByteArray())
                outputStream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun stop() {
        isRunning.set(false)

        clientSockets.forEach { socket ->
            try {
                socket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        clientSockets.clear()

        try {
            serverSocket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}