package tw.waterballsa.gaas.exceptions.enums

enum class SocketIOEventMessage(val eventName: String) {
    CHAT_MESSAGE("CHAT_MESSAGE"),
    CHATROOM_JOIN("CHATROOM_JOIN"),
    CONNECT_EVENT("CONNECT_EVENT"),
}
