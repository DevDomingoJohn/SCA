# ServerSocket Implementation

[ServerSocket Class](/app/src/main/java/com/domin/sca/core/network/ServerSocket.kt)
```kotlin
/**
 * A simple TCP server implementation that handles one client at a time.
 * Demonstrates basic socket programming concepts in Android.
 *
 * Key Concepts Shown:
 * - Server socket creation and port binding
 * - Client connection management
 * - Thread-per-client model
 * - Basic network I/O operations
 * - Graceful shutdown handling
 *
 * @param port The TCP port to listen on (e.g., 8888)
 * @param addLog Callback for logging server events to UI
 */
class ServerSocket(
    private val port: Int,
    val addLog: (String) -> Unit
) {
    // The actual server socket instance
    private lateinit var serverSocket: java.net.ServerSocket

    // Output stream for the currently connected client
    private lateinit var outputStream: OutputStream

    // Thread-safe flag for server running state
    private val isRunning = AtomicBoolean(true)

    // Track connected clients (limited to 1 in current implementation)
    private val clientSockets = mutableListOf<Socket>()

    /**
     * Starts the server in a background thread to prevent UI blocking.
     * Uses a while loop to continuously accept new client connections.
     * Implements basic client capacity control (max 1 client).
     */
    fun start() {
        Thread {
            try {
                serverSocket = java.net.ServerSocket(port)
                addLog("Started Server on port: $port")

                // Main accept loop - runs until stop() is called
                while(isRunning.get()) {
                    val socket = serverSocket.accept()

                    // Capacity control: Only allow one client
                    if (clientSockets.isNotEmpty()) {
                        // Immediately reject new connections when full
                        val tempOutputStream = socket.getOutputStream()
                        tempOutputStream.write("Server is Full!".toByteArray())
                        tempOutputStream.flush()
                        socket.close()
                        continue  // Skip to next accept() call
                    }

                    addLog("${socket.inetAddress.hostAddress} Joined The Server")

                    // Set up output stream for this client
                    outputStream = socket.getOutputStream()
                    outputStream.write("Welcome To Soul Society!".toByteArray())
                    outputStream.flush()

                    clientSockets.add(socket)

                    // Start client handler thread
                    Thread {
                        handleClient(socket)
                    }.start()
                }
            } catch (e: IOException) {
                // Expected exception when serverSocket is closed intentionally
                if (isRunning.get()) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    /**
     * Handles communication with a connected client.
     * Runs in a separate thread to allow simultaneous I/O.
     *
     * @param client The connected client's socket instance
     */
    private fun handleClient(client: Socket) {
        try {
            val inputStream = client.getInputStream()

            // Continuous read loop for client messages
            while(isRunning.get()) {
                val buffer = ByteArray(1024)  // Fixed-size buffer
                val bytesRead = inputStream.read(buffer) // Blocks until data receive

                // Connection closed by client
                if (bytesRead == -1) break

                val message = String(buffer, 0, bytesRead)
                addLog("${client.inetAddress.hostAddress}: $message")
            }

        } catch (e: IOException) {
            addLog("Error handling client: ${e.message}")
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
                addLog("Failed to send message: ${e.message}")
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
            addLog("Error stopping server: ${e.message}")
        }
    }
}
```

## Key Implementation Notes:
1. **Thread Management:**
   - Uses raw `Thread` instead of coroutines for explicit demonstration
   - One thread for accept loop + one per client handler 
   - `AtomicBoolean` ensures thread-safe state checks

2. **Client Limitations:**
   - Only allows 1 concurrent client (for simplicity)
   - Immediate rejection message for excess clients

3. **I/O Considerations:**
   - Uses blocking I/O operations (`accept()`, `read()`)
   - Fixed-size buffer (1024 bytes) limits message size
   - No encoding handling (assumes ASCII/UTF-8)

4. **Error Handling:**
   - Basic exception catching with stack traces
   - Graceful cleanup in `finally` blocks
   - UI feedback via `addLog` callback

5. **Usage Example in ViewModel:**
   ```kotlin
   // Start server on port
   fun startServer(port: Int) {
        viewModelScope.launch {
            serverSocket = ServerSocket(port) {
                addLog(it)
            }
            serverSocket.start()
        }
    }
   ```
   ```kotlin
   // Send message to client
   fun message(text: String) {
        viewModelScope.launch {
            serverSocket.message(text)
            addLog("Me: $text")
        }
    }
   ```
   ```kotlin
   // Stop server
   fun stopServer() {
        viewModelScope.launch {
            serverSocket.stop()
        }
    }
   ```
   [Check ServerVM Class For More Details About The Usage](/app/src/main/java/com/domin/sca/server/ServerVM.kt)

## Edge Cases to Consider:
1. **Network Permissions:** Requires `<uses-permission android:name="android.permission.INTERNET"/>`
2. **Port Conflicts:** Handle `BindException` if port is occupied
3. **Partial Writes:** `OutputStream.write()` may not send all bytes
4. **Device Sleep:** Network operations may fail when device sleeps