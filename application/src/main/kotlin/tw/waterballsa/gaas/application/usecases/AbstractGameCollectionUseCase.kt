package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.enums.PlatformError
import tw.waterballsa.gaas.exceptions.enums.PlatformError.GAME_NOT_FOUND

abstract class AbstractGameCollectionUseCase(
    protected val userRepository: UserRepository,
    protected val gameRegistrationRepository: GameRegistrationRepository,
) {
    protected fun findUser(identityProviderId: String): User {
        return userRepository.findByIdentity(identityProviderId)
            ?: throw notFound(PlatformError.USER_NOT_FOUND, User::class).message()
    }

    protected fun findGameRegistrationById(gameId: String) =
        gameRegistrationRepository.findById(GameRegistration.Id(gameId))
            ?: throw notFound(GAME_NOT_FOUND, GameRegistration::class).id(gameId)
}
