package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.events.UserUpdatedEvent
import tw.waterballsa.gaas.exceptions.PlatformException
import javax.inject.Named

@Named
class UpdateUserUseCase(
    private val userRepository: UserRepository,
    private val eventBus: EventBus
) {
    fun execute(request: Request, presenter: Presenter) {
        userRepository.run {
            val user = when {
                existsUserByNickname(request.nickname) -> throw PlatformException("Nickname already exists.")
                else -> {
                    findByEmail(request.email)
                        ?: throw PlatformException("User not found.")
                }
            }
            user.nickname = request.nickname
            updateUser(user)
        }.also { user ->
            val event = user.toUserUpdatedEvent()
            presenter.present(event)
            eventBus.broadcast(event)
        }
    }

    data class Request(val email: String, val nickname: String)
}

private fun User.toUserUpdatedEvent(): UserUpdatedEvent =
    UserUpdatedEvent(id!!, email, nickname)
