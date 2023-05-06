package tw.waterballsa.gaas.spring.repositories

import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.spring.repositories.dao.GameRegistrationDAO
import tw.waterballsa.gaas.spring.repositories.dao.RoomDAO

class SpringRoomRepository(
    private val roomDAO : RoomDAO
) : RoomRepository {
    override fun findByRoomId(roomId: String) : Room? =
        roomDAO.findByRoomId(roomId)?.toDomain()

    override fun deleteAll() {
        roomDAO.deleteAll()
    }

}