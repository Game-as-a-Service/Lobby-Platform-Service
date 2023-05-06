package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.Room

interface RoomRepository {
:    fun findByRoomId(roomId: Room.RoomId): Room?
    fun deleteAll()
}