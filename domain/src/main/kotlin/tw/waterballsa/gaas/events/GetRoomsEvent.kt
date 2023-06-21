package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.Room

data class GetRoomsEvent(
    val rooms: List<Room>,
    val page: Page
) : DomainEvent() {

    data class Page(
        val total: Int,
        val page: Int,
        val offset: Int
    )
}
