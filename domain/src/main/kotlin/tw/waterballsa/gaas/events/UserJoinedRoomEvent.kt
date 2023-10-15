package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.events.enums.EventMessageType

data class UserJoinedRoomEvent(
    val type: EventMessageType,
    val data: Data
) : DomainEvent() {
    data class Data(
        val user: UserInfo,
        val roomId: String
    ) {
        data class UserInfo(
            val id: String,
            val nickname: String
        )
    }
}