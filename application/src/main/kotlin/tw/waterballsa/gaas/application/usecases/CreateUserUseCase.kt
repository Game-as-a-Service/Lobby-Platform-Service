package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.events.UserCreatedEvent
import java.util.UUID.randomUUID
import javax.inject.Named

@Named
class CreateUserUseCase(
    private val userRepository: UserRepository,
    private val eventBus: EventBus,
) {
    fun execute(request: Request) {
        val user = userRepository.findByEmail(request.email)

        with (request) {
            when {
                user == null -> createUser()
                !user.hasIdentity(identityProviderId) -> user.addUserIdentity(identityProviderId)
            }
        }
    }

    private fun Request.createUser() {
        val user = userRepository.createUser(toUser())
        val event = user.toUserCreatedEvent()
        eventBus.broadcast(event)
    }

    private fun User.addUserIdentity(identityProviderId: String) {
        addIdentity(identityProviderId)
        userRepository.update(this)
    }

    class Request(
        val email: String,
        val identityProviderId: String,
    )

    private fun Request.toUser(): User = User(
        email = email,
        nickname = "user_${randomUUID()}",
        identities = mutableListOf(identityProviderId)
    )
}

fun User.toUserCreatedEvent(): UserCreatedEvent = UserCreatedEvent(id!!, email, nickname)
