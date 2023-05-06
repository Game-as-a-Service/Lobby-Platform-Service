package tw.waterballsa.gaas.spring.repositories.dao

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.spring.repositories.data.GameRegistrationData
import tw.waterballsa.gaas.spring.repositories.data.RoomData

@Repository
interface RoomDAO : MongoRepository<RoomData, String> {
    fun findByRoomId(roomId: String): RoomData?
}