package tw.waterballsa.gaas.events.enums

enum class EventMessageType(
    val eventName: String
) {
    CHAT_MESSAGE("CHAT_MESSAGE"),
    CHATROOM_JOIN("CHATROOM_JOIN"),
    CONNECT_EVENT("CONNECT_EVENT"),
    GAME_STARTED("GAME_STARTED"),
}
