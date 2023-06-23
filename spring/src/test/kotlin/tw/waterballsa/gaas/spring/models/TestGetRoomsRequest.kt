package tw.waterballsa.gaas.spring.models

import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.domain.Room

class TestGetRoomsRequest(
    val status: String,
    val page: Int,
    val offset: Int
) {
    fun toStatus() : Room.Status = Room.Status.valueOf(status)
    fun toPagination(): Pagination<Any> = Pagination(page, offset)
}
