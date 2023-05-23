package tw.waterballsa.gaas.spring.repositories.dao

import org.springframework.data.mongodb.repository.MongoRepository
import tw.waterballsa.gaas.spring.repositories.data.RoomData

interface RoomDAO : MongoRepository<RoomData, String> {
}