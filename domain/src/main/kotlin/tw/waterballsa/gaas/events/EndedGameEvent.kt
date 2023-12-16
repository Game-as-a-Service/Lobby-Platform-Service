package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.events.enums.EventMessageType

class EndedGameEvent(
    type: EventMessageType,
    val data: Data
) : SocketIoResponseEvent(type) {
    data class Data(
        val roomId: String,
    )


    override fun getEventData(): Any = data
}
