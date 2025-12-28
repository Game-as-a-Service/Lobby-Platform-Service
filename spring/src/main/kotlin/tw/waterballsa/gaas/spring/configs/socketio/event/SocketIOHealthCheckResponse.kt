package tw.waterballsa.gaas.spring.configs.socketio.event

import java.time.Instant

data class SocketIOHealthCheckResponse(
    val status: String = "UP",
    val timestamp: String = Instant.now().toString(),
    val service: String = "Lobby Platform WebSocket Service"
)
