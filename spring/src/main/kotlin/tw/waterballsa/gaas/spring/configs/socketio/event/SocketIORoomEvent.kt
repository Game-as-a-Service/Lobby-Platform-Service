package tw.waterballsa.gaas.spring.configs.socketio.event

data class SocketIORoomEvent(
    val target: String = "",
    val user: SocketIOUser = SocketIOUser("", ""),
)

