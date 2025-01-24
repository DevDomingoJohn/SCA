package com.domin.sca.core.network

import android.util.Log
import java.io.IOException
import java.io.OutputStream
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class ClientSocket(
    private val ip: String,
    private val port: Int
) {
    private lateinit var socket: Socket
    private lateinit var outputStream: OutputStream
    private val isConnected = AtomicBoolean(true)

    fun connect() {
        Thread {
            try {
                socket = Socket(ip,port)

                outputStream = socket.getOutputStream()
                val client = "Hello I'm ${socket.inetAddress.hostAddress}"
                outputStream.write(client.toByteArray())
                outputStream.flush()

                val inputStream = socket.getInputStream()
                while(isConnected.get()) {
                    val buffer = ByteArray(1024)
                    val bytesRead = inputStream.read(buffer)
                    val message = String(buffer,0,bytesRead)
                    Log.i("Server To Client",message)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
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

    fun disconnect() {
        isConnected.set(false)
        socket.close()
        Log.i("Client Socket","Disconnected From The Server")
    }
}