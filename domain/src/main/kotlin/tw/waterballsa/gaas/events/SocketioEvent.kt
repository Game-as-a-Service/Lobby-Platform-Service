package tw.waterballsa.gaas.events

data class SocketioEvent(
    var type: String = "",
    var data: ChatData = ChatData()
) {
    constructor(type: String, userId: String, nickname: String, target: String) : this(
        type,
        ChatData(ChatUser(userId, nickname), target)
    )
}


data class ChatData(
    var user: ChatUser = ChatUser(),
    var target: String = ""
)

data class ChatUser(
    var id: String = "",
    var nickname: String = ""
)