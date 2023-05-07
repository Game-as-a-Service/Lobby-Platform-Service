package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User

interface RoomRepository {
    fun createRoom(room: Room): Room
    fun findByRoomId(roomId: Room.Id): Room?
    fun deleteAll()
    fun existsByHostId(userId: User.UserId): Boolean
}

