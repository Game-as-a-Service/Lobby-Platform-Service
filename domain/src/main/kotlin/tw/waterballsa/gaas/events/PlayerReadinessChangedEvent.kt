package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.events.enums.EventMessageType

class PlayerReadinessChangedEvent(
    type: EventMessageType,
    val data: Data
) : SocketIoResponseEvent(type) {
    data class Data(
        val user: User,
        val roomId: String,
    ) {
        data class User(
            val id: String,
            val nickname: String,
        )
    }

    override fun getEventData(): Any = data
}
