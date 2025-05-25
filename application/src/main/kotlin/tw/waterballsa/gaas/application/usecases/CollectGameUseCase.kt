package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.GameCollectionRepository
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameCollection
import javax.inject.Named

@Named
class CollectGameUseCase(
    userRepository: UserRepository,
    gameRegistrationRepository: GameRegistrationRepository,
    private val gameCollectionRepository: GameCollectionRepository,
) : AbstractGameCollectionUseCase(userRepository, gameRegistrationRepository) {

    fun execute(request: Request) {
        val user = findUser(request.identityProviderId)
        val game = findGameRegistrationById(request.gameId)
        val gameCollection = gameCollectionRepository.findByGameIdAndUserId(game.id!!, user.id!!)
        if (gameCollection != null) {
            return
        }
        gameCollectionRepository.collectGame(GameCollection(game.id!!, user.id!!))
    }

    data class Request(
        val identityProviderId: String,
        val gameId: String,
    )
}
