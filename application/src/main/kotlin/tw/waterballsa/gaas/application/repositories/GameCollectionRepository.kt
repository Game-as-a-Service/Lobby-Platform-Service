package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.GameCollection
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User

interface GameCollectionRepository {
    fun collectGame(gameCollection: GameCollection)
    fun findByGameIdAndUserId(gameId: GameRegistration.Id, userId: User.Id): GameCollection?
    fun unCollectGame(gameId: GameRegistration.Id, userId: User.Id)
}
