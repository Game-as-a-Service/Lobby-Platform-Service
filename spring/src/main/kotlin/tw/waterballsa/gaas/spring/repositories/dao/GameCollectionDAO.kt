package tw.waterballsa.gaas.spring.repositories.dao

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import tw.waterballsa.gaas.spring.repositories.data.GameCollectionData

@Repository
interface GameCollectionDAO : MongoRepository<GameCollectionData, String> {
    fun deleteByGameIdAndUserId(gameId: String, userId: String): Long

    fun findByGameIdAndUserId(gameId: String, userId: String): GameCollectionData?
}