package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User

interface RoomRepository {
    fun deleteAll()
    fun createRoom(room: Room): Room
    fun findById(roomId: Room.Id): Room?
    fun existsByHostId(hostId: User.Id): Boolean
    fun joinRoom(room: Room): Room
    fun findByStatus(status: Room.Status, page: Pagination<Any>): Pagination<Room>
}
