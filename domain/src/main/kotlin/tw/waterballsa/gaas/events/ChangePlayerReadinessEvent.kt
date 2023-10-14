package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.events.enums.EventMessageType

data class ChangePlayerReadinessEvent(
    val type: EventMessageType,
    val data: Data
) : DomainEvent() {
    data class Data(
        val user: User,
        val roomId: String
    ) {
        data class User(
            val id: String,
            val nickname: String
        )
    }
}