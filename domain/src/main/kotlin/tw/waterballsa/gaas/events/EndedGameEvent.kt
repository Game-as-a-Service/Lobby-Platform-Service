package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.enums.EventMessageType

class EndedGameEvent(
    type: EventMessageType,
    val data: Data,
) : RoomEvent(type) {
    data class Data(
        val roomId: Room.Id,
    )

    override fun getEventData(): Any = data

    override fun getRoomId(): Room.Id = data.roomId
}
