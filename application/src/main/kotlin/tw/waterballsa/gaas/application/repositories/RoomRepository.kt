package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.Room

interface RoomRepository {

    fun createRoom(room: Room): Room
    fun findById(id: Room.Id): Room?
    fun joinRoom(room: Room): Room
    fun deleteAll()
}