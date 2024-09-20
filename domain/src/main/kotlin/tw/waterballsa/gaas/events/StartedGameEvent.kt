package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.enums.EventMessageType

class StartedGameEvent(
    type: EventMessageType,
    val data: Data,
) : RoomEvent(type) {
    data class Data(
        val gameUrl: String,
        val roomId: Room.Id,
        val gameId: GameRegistration.Id,
        val players: List<Player>,
    ) {
        data class Player(
            val id: String,
            val nickname: String,
        )
    }

    override fun getEventData(): Any = data

    override fun getRoomId(): Room.Id = data.roomId
}
