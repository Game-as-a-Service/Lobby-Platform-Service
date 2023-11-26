package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.events.enums.EventMessageType

data class EndedGameEvent(
    val type: EventMessageType,
    val data: Data,
) : DomainEvent() {
    data class Data(
        val roomId: String,
    )
}
