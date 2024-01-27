package tw.waterballsa.gaas.spring.configs.socketio.event


data class SocketIOChatEvent(
    val from: SocketIOUser = SocketIOUser("", ""),
    val content: String = "",
    val target: String = "",
    var timestamp: String? = null,
){
    fun isLobby() = target == "LOBBY"
}