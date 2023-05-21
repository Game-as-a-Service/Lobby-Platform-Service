package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.events.UserCreatedEvent
import javax.inject.Named

@Named
class CreateUserUseCase(
    private val userRepository: UserRepository,
    private val eventBus: EventBus
) {
    fun execute(request: Request) = when {
        userRepository.existsUserByEmail(request.email) -> {}
        else -> {
            val user = userRepository.createUser(request.toUser())
            val event = user.toUserCreatedEvent()
            eventBus.broadcast(event)
        }
    }

    class Request(val email: String) {
        fun toUser(): User = User(email = email, nickname = "")
    }
}

fun User.toUserCreatedEvent(): UserCreatedEvent = UserCreatedEvent(id!!, email, nickname)
