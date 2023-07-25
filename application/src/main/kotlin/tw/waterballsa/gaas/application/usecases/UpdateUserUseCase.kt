package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.events.UserUpdatedEvent
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.PlatformException
import javax.inject.Named

@Named
class UpdateUserUseCase(
    private val userRepository: UserRepository,
    private val eventBus: EventBus,
) {
    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            validateNicknameDuplicated(nickname)
            val user = findUserByIdentity(userIdentity)
            user.changeNickname(nickname)
            val updatedUser = userRepository.update(user)

            val event = updatedUser.toUserUpdatedEvent()
            presenter.present(event)
            eventBus.broadcast(event)
        }
    }

    private fun validateNicknameDuplicated(nickname: String) {
        if (userRepository.existsUserByNickname(nickname)) {
            throw PlatformException("invalid nickname: duplicated")
        }
    }

    private fun findUserByIdentity(userIdentity: String) =
        userRepository.findByIdentity(userIdentity)
            ?: throw notFound(User::class).identifyBy("userIdentity", userIdentity)

    data class Request(val userIdentity: String, val nickname: String)
}

private fun User.toUserUpdatedEvent(): UserUpdatedEvent =
    UserUpdatedEvent(id!!, email, nickname)
