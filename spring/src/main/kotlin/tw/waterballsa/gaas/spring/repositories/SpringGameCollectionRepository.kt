package tw.waterballsa.gaas.spring.repositories

import org.springframework.stereotype.Repository
import tw.waterballsa.gaas.application.repositories.GameCollectionRepository
import tw.waterballsa.gaas.domain.GameCollection
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.repositories.dao.GameCollectionDAO
import tw.waterballsa.gaas.spring.repositories.data.toData

@Repository
class SpringGameCollectionRepository(
    private val gameCollectionDAO: GameCollectionDAO,
) : GameCollectionRepository {
    override fun collectGame(gameCollection: GameCollection) {
        gameCollectionDAO.save(gameCollection.toData())
    }

    override fun findByGameIdAndUserId(gameId: GameRegistration.Id, userId: User.Id): GameCollection? {
        return gameCollectionDAO.findByGameIdAndUserId(gameId.value, userId.value)?.toDomain()
    }

    override fun unCollectGame(gameId: GameRegistration.Id, userId: User.Id) {
        gameCollectionDAO.deleteByGameIdAndUserId(gameId.value, userId.value)
    }
}