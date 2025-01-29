package com.domin.sca.core.network

import java.io.IOException
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A simple TCP server implementation that handles one client at a time.
 * Demonstrates basic socket programming concepts in Android.
 *
 * @param port The TCP port to listen on (e.g., 8888)
 * @param addLog Callback for logging server events to UI
 */
class ServerSocket(
    private val port: Int,
    val addLog: (String) -> Unit
) {
    // The actual server socket instance
    private lateinit var serverSocket: ServerSocket

    // Output stream for the currently connected client
    private lateinit var outputStream: OutputStream

    // Thread-safe flag for server running state
    private val isRunning = AtomicBoolean(true)

    // Track connected clients (limited to 1 in current implementation)
    private val clientSockets = mutableListOf<Socket>()

    /**
     * Starts the server in a background thread to prevent UI blocking.
     */
    fun start() {
        Thread {
            try {
                serverSocket = ServerSocket(port)
                addLog("Started Server on port: $port")

                // Main accept loop - runs until stop() is called
                while(isRunning.get()) {
                    val socket = serverSocket.accept()

                    // Capacity control: Only allow one client
                    if (clientSockets.isNotEmpty()){
                        // Immediately reject new connections when full
                        val tempOutputStream = socket.getOutputStream()
                        val server = "Server is Full!"
                        tempOutputStream.write(server.toByteArray())
                        tempOutputStream.flush()
                        socket.close()
                        continue // Skip to next accept() call
                    }

                    addLog("${socket.inetAddress.hostAddress} Joined The Server")

                    // Set up output stream for this client
                    outputStream = socket.getOutputStream()
                    val server = "Welcome To Soul Society!"
                    outputStream.write(server.toByteArray())
                    outputStream.flush()

                    clientSockets.add(socket)

                    // Start client handler thread
                    Thread {
                        handleClient(socket)
                    }.start()
                }
            } catch (e: IOException) {
                // Expected exception when serverSocket is closed intentionally
                if (isRunning.get()) e.printStackTrace()
            }
        }.start()
    }

    /**
     * Handles communication with a connected client.
     *
     * @param client The connected client's socket instance
     */
    private fun handleClient(client: Socket) {
        try {
            val inputStream = client.getInputStream()

            // Continuous read loop for client messages
            while(isRunning.get()) {
                val buffer = ByteArray(1024) // Fixed-size buffer
                val bytesRead = inputStream.read(buffer) // Blocks until data receive

                // Connection closed by client
                if (bytesRead == -1) break

                val message = String(buffer,0,bytesRead)
                addLog("${client.inetAddress.hostAddress}: $message")
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // Cleanup on disconnect
            client.close()
            clientSockets.remove(client)
            addLog("${client.inetAddress.hostAddress} Disconnected!")
        }
    }

    /**
     * Sends a message to the currently connected client.
     * Runs in background thread to prevent UI blocking.
     *
     * @param text The message to send (ASCII only in current implementation)
     */
    fun message(text: String) {
        Thread {
            try {
                // Writes to the last established client connection
                outputStream.write(text.toByteArray())
                outputStream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    /**
     * Stops the server gracefully:
     * 1. Sets running flag to false
     * 2. Closes the server socket
     * 3. Closes all client connections
     */
    fun stop() {
        isRunning.set(false)

        try {
            // Force-close server socket to break out of accept() blocking
            serverSocket.close()

            // Close all connected clients
            clientSockets.forEach { it.close() }
            clientSockets.clear()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}