package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.extension.toRoomPlayer
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound

abstract class AbstractRoomUseCase(
    protected val userRepository: UserRepository,
) {
    fun findPlayerByIdentity(userIdentity: String): Room.Player =
        userRepository.findByIdentity(userIdentity)
            ?.toRoomPlayer()
            ?: throw notFound(User::class).message()
}
