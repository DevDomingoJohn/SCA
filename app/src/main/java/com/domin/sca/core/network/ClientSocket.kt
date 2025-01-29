package com.domin.sca.core.network

import java.io.IOException
import java.io.OutputStream
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A basic TCP client implementation for Android that connects to a server socket.
 * Demonstrates core client-side networking concepts.
 *
 * @param ip Server IP address to connect to
 * @param port Server port number
 * @param addLog Callback for real-time connection logging
 */
class ClientSocket(
    private val ip: String,
    private val port: Int,
    private val addLog: (String) -> Unit
) {
    private lateinit var socket: Socket
    private lateinit var outputStream: OutputStream
    private val isConnected = AtomicBoolean(false) // Thread-safe connection flag

    /**
     * Establishes connection to the server in a background thread.
     * Starts message reception loop upon successful connection.
     */
    fun connect() {
        Thread {
            try {
                // 1. Create socket connection (blocks until connected or timeout)
                socket = Socket(ip,port)
                isConnected.set(true)

                // 2. Set up output stream for sending messages
                outputStream = socket.getOutputStream()

                // 3. Start message reception loop
                val inputStream = socket.getInputStream()
                while(isConnected.get()) {
                    val buffer = ByteArray(1024) // Fixed-size receive buffer
                    val bytesRead = inputStream.read(buffer) // Blocks until data receive

                    if (bytesRead == -1) break // Server closed connection

                    val message = String(buffer,0,bytesRead)
                    addLog("${socket.inetAddress.hostAddress}: $message")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                disconnect() // Ensure cleanup on any failure
            }
        }.start()
    }

    /**
     * Sends text message to the server in a background thread.
     * @param text Plain text message to send (no encoding handling)
     */
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

    /**
     * Gracefully closes the connection:
     * 1. Closes socket and streams
     * 2. Updates connection state
     * 3. Triggers UI update via addLog
     */
    fun disconnect() {
        if (isConnected.get()) {
            isConnected.set(false)
            try {
                socket.close() // Also closes input/output streams
            } catch (e: IOException) {
                e.printStackTrace()
            }
            addLog("You Disconnected From The Server")
        }
    }
}