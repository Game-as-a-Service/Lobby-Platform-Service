package tw.waterballsa.gaas.spring.controllers

import com.corundumstudio.socketio.SocketIOServer
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
class WebSocketHealthCheckController(
    private val socketIOServer: SocketIOServer
) {
    @GetMapping("/health/websocket")
    fun healthCheck(): ResponseEntity<WebSocketHealthStatus> {
        val isRunning = try {
            val namespaces = socketIOServer.allNamespaces
            namespaces.isNotEmpty() && socketIOServer.configuration != null
        } catch (e: Exception) {
            false
        }
        
        return ResponseEntity.ok(WebSocketHealthStatus(
            status = if (isRunning) "UP" else "DOWN",
            timestamp = Instant.now().toString(),
            service = "Lobby Platform WebSocket Service",
            clients = if (isRunning) socketIOServer.allClients.size else 0
        ))
    }
}

data class WebSocketHealthStatus(
    val status: String,
    val timestamp: String,
    val service: String,
    val clients: Int
)
