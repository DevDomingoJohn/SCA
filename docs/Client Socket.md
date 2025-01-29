# ClientSocket Class Reference

```kotlin
/**
 * A basic TCP client implementation for Android that connects to a server socket.
 * Demonstrates core client-side networking concepts.
 *
 * Key Learning Points:
 * - Establishing client-server connections
 * - Thread management for network operations
 * - Bidirectional communication (sending/receiving)
 * - Connection state management
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
            try {
                socket.close() // Also closes input/output streams
            } catch (e: IOException) {
                e.printStackTrace()
            }
            addLog("You Disconnected From The Server")
            isConnected.set(false)
        }
    }
}
```

## Key Implementation Notes:
1. Thread Management:
   - Dedicated thread for connection setup/message reception
   - Separate thread for each message send operation
   - AtomicBoolean ensures safe state checks across threads

2. I/O Handling:
    ```
    val buffer = ByteArray(1024)  // Fixed buffer size
    val bytesRead = inputStream.read(buffer)  // Blocking call
   ```
   - Maximum message size limited to 1024 bytes
   - Blocking read waits indefinitely for server messages

3. Error Handling:
   - Automatic cleanup in `finally` block
   - UI feedback via `addLog` callback
   - Connection state reset on any failure

4. Usage Example in ViewModel:
   ```kotlin
   // Create client instance & Connect to server
   fun connectToServer(ip: String, port: Int) { 
       viewModelScope.launch { 
           clientSocket = ClientSocket(ip,port) { 
               addLog(it) 
           }
          clientSocket.connect() 
       }
   }
   ```
   ```kotlin
   // Send message
   fun message(text: String) { 
       viewModelScope.launch { 
           clientSocket.message(text)
          addLog("Me: $text") 
       } 
   }
   ```
   ```kotlin
   // Disconnect when done
   fun disconnect() { 
       viewModelScope.launch { 
           clientSocket.disconnect() 
       }
   }
   ```

5. Common Pitfalls Addressed:
   1. UI Blocking:
      - All network operations run in background threads
      - `addLog` callback handles UI updates safely
      
   2. Connection State Management:
        ```
      if (isConnected.get()) { ... }  // Check before socket operations
      ```
      - Prevents operations on closed sockets

   3. Resource Cleanup:
      - `socket.close()` in `disconnect()` releases system resources
      - Streams closed automatically with socket

## Edge Cases to Consider:
1. Network Unavailable: Handle during initial connection
2. Server Disconnects: Detect via `read() == -1`
3. Partial Writes: `flush()` ensures full message transmission
4. Encoding Issues: Assumes UTF-8 (no explicit charset handling)