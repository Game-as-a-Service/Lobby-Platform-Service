package tw.waterballsa.gaas.spring.repositories

import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.spring.repositories.dao.GameRegistrationDAO
import tw.waterballsa.gaas.spring.repositories.dao.RoomDAO

class SpringRoomRepository(
    private val roomDAO : RoomDAO
) : RoomRepository {
    override fun findByRoomId(roomId: Room.RoomId) : Room? =
        roomDAO.findByRoomId(roomId.value)?.toDomain()

    override fun deleteAll() {
        roomDAO.deleteAll()
    }

}