package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.events.enums.EventMessageType

data class PlayerJoinedRoomEvent(
    val type: EventMessageType,
    val data: Data,
) : DomainEvent() {
    data class Data(
        val user: Player,
        val roomId: String,
    ) {
        data class Player(
            val id: String,
            val nickname: String,
        )
    }
}
