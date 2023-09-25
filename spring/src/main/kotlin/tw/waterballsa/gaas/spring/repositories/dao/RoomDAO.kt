package tw.waterballsa.gaas.spring.repositories.dao

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.spring.repositories.data.GameRegistrationData
import tw.waterballsa.gaas.spring.repositories.data.RoomData

@Repository
interface RoomDAO : MongoRepository<RoomData, String> {
    fun existsByHostId(hostId: String): Boolean
    fun findByStatus(status: Room.Status, pageable: Pageable): Page<RoomData>
    fun existsByPlayersIdIn(playerIds: Collection<String>): Boolean
    fun findAllByStatusAndGameAndPasswordNull(status: Room.Status, game: GameRegistrationData): List<RoomData>
}

