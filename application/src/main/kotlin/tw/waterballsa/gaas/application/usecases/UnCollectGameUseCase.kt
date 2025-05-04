package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.GameCollectionRepository
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import javax.inject.Named

@Named
class UnCollectGameUseCase(
    userRepository: UserRepository,
    gameRegistrationRepository: GameRegistrationRepository,
    private val gameCollectionRepository: GameCollectionRepository,
) : AbstractGameCollectionUseCase(userRepository, gameRegistrationRepository) {

    fun execute(request: Request) {
        val user = findUser(request.identityProviderId)
        val game = findGameRegistrationById(request.gameId)
        gameCollectionRepository.unCollectGame(game.id!!, user.id!!)
    }

    data class Request(
        val identityProviderId: String,
        val gameId: String,
    )
}
