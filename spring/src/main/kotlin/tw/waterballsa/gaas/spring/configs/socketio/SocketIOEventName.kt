package tw.waterballsa.gaas.spring.configs.socketio

enum class SocketIOEventName (val eventName: String){
    CHAT_MESSAGE("CHAT_MESSAGE"),
    CHATROOM_JOIN("CHATROOM_JOIN"),
    JOIN_ROOM("JOIN_ROOM"),
    LEAVE_ROOM("LEAVE_ROOM"),
    CONNECT_EVENT("CONNECT_EVENT"),
    DISCONNECT("DISCONNECT");
}

