package tw.waterballsa.gaas.spring.repositories.dao

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import tw.waterballsa.gaas.spring.repositories.data.GameCommentData

@Repository
interface GameCommentDAO: MongoRepository<GameCommentData, String> {
    fun findByGameIdAndUserId(gameId: String, userId: String): GameCommentData?
}