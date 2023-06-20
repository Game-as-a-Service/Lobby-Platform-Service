package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.Room

data class GetRoomsEvent(
    val rooms: List<Room>,
    val meta: Meta
) : DomainEvent() {

    data class Meta(
        val total: Int,
        val page: Int,
        val offset: Int
    )
}
