package tw.waterballsa.gaas.domain

import lombok.Data




@Data
class Message(
    val type: MessageType = MessageType.SERVER,
    var message: String,
    var room: String) {
    //constructor() : this(MessageType.SERVER, "")
    constructor(type: MessageType, message: String) : this(type, message, "")
}


enum class MessageType {
    SERVER,
    CLIENT
}