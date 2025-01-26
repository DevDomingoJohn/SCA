package com.domin.sca.core.network

import java.io.IOException
import java.io.OutputStream
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class ClientSocket(
    private val ip: String,
    private val port: Int,
    private val addLog: (String) -> Unit
) {
    private lateinit var socket: Socket
    private lateinit var outputStream: OutputStream
    private val isConnected = AtomicBoolean(false)

    fun connect() {
        Thread {
            try {
                socket = Socket(ip,port)
                isConnected.set(true)

                outputStream = socket.getOutputStream()

                val inputStream = socket.getInputStream()
                while(isConnected.get()) {
                    val buffer = ByteArray(1024)
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead == -1) break
                    val message = String(buffer,0,bytesRead)
                    addLog("${socket.inetAddress.hostAddress}: $message")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                disconnect()
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
        if (isConnected.get()) {
            try {
                socket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            addLog("You Disconnected From The Server")
            isConnected.set(false)
        }
    }
}