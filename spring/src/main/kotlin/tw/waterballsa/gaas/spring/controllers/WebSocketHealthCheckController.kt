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
    @GetMapping("/websocket/health")
    fun healthCheck(): ResponseEntity<WebSocketHealthStatus> {
        val isRunning = socketIOServer.isStarted && !socketIOServer.isShuttingDown
        
        return ResponseEntity.ok(WebSocketHealthStatus(
            status = if (isRunning) "UP" else "DOWN",
            timestamp = Instant.now().toString(),
            service = "Lobby Platform WebSocket Service",
            clients = socketIOServer.allClients.size,
            rooms = socketIOServer.allRooms.size
        ))
    }
}

data class WebSocketHealthStatus(
    val status: String,
    val timestamp: String,
    val service: String,
    val clients: Int,
    val rooms: Int
)
