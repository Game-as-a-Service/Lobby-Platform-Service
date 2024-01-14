package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.enums.EventMessageType

class PlayerReadinessChangedEvent(
    type: EventMessageType,
    val data: Data
) : RoomEvent(type) {
    data class Data(
        val user: User,
        val roomId: Room.Id,
    ) {
        data class User(
            val id: String,
            val nickname: String,
        )
    }

    override fun getEventData(): Any = data

    override fun getRoomId(): Room.Id = data.roomId
}
