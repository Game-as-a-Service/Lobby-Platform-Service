package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.enums.EventMessageType

class StartedGameEvent(
    type: EventMessageType,
    val data: Data
) : SocketIoResponseEvent(type) {
    data class Data(
        val gameUrl: String,
        val roomId: Room.Id,
    )


    override fun getEventData(): Any = data
}
