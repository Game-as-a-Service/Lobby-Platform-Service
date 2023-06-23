package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.events.UserCreatedEvent
import java.util.UUID
import javax.inject.Named

@Named
class CreateUserUseCase(
    private val userRepository: UserRepository,
    private val eventBus: EventBus,
) {
    fun execute(request: Request) {
        var user = userRepository.findByEmail(request.email)

        when {
            user == null -> {
                user = userRepository.createUser(request.toUser())
                val event = user.toUserCreatedEvent()
                eventBus.broadcast(event)
            }

            user.doesNotHaveIdentity(request.identityProviderId) -> {
                user = user.addIdentity(request.identityProviderId)
                userRepository.update(user)
            }
        }
    }

    class Request(
        val email: String,
        val identityProviderId: String,
    ) {
        fun toUser(): User = User(
            email = email,
            nickname = "user_${UUID.randomUUID()}",
            identities = listOf(identityProviderId)
        )
    }
}

fun User.toUserCreatedEvent(): UserCreatedEvent = UserCreatedEvent(id!!, email, nickname)
