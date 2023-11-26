package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.enums.EventMessageType

data class StartedGameEvent(
    val type: EventMessageType,
    val data: Data,
) : DomainEvent() {

    data class Data(
        val gameUrl: String,
        val roomId: Room.Id,
    )
}
