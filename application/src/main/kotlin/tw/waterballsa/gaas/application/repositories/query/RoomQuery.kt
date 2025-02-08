package tw.waterballsa.gaas.application.repositories.query

import tw.waterballsa.gaas.domain.Room.Status

data class RoomQuery(
    val status: Status,
    val public: Boolean?,
    val keyword: String?,
)
