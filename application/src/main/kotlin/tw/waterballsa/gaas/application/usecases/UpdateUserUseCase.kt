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
            validateNicknameLength(nickname)
            val user = findUserByEmail(email)
            val updatedUser = user.updateNickname(request.nickname)
            userRepository.update(updatedUser)

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

    private fun validateNicknameLength(nickname: String) {
        if (nickname.toByteArray().size < 4) {
            throw PlatformException("invalid nickname: too short")
        }
        if (nickname.toByteArray().size > 16) {
            throw PlatformException("invalid nickname: too long")
        }
    }

    private fun findUserByEmail(email: String) =
        userRepository.findByEmail(email)
            ?: throw notFound(User::class).identifyBy("email", email)

    data class Request(val email: String, val nickname: String)
}

private fun User.toUserUpdatedEvent(): UserUpdatedEvent =
    UserUpdatedEvent(id!!, email, nickname)
