package tw.waterballsa.gaas.spring.models

import tw.waterballsa.gaas.application.model.Pageable
import tw.waterballsa.gaas.application.repositories.query.RoomQuery
import tw.waterballsa.gaas.domain.Room

class TestGetRoomsRequest(
    val status: String,
    val public: Boolean?,
    val keyword: String?,
    val page: Int,
    val perPage: Int
) {
    fun toQuery() : RoomQuery = RoomQuery(Room.Status.valueOf(status), public, keyword)
    fun toPageable(): Pageable = Pageable(page, perPage)
}
