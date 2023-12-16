package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.events.enums.EventMessageType

class PlayerLeavedRoomEvent(
    type: EventMessageType,
    val data: Data
) : SocketIoResponseEvent(type) {
    data class Data(
        val user: Player,
        val roomId: String,
    ) {
        data class Player(
            val id: String,
            val nickname: String,
        )
    }


    override fun getEventData(): Any = data

}
