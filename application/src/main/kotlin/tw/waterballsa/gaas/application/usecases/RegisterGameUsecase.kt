package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.events.RegisteredGameEvent
import javax.inject.Named

@Named
class RegisterGameUsecase(
    private val gameRegistrationRepository: GameRegistrationRepository
) {
    fun execute(request: Request, presenter: Presenter) {
        val newGameRegistration = gameRegistrationRepository.run {
            with(request) {
                findGameRegistrationByUniqueName(uniqueName)?.let { throw IllegalArgumentException("Game already exists") }
                registerGame(GameRegistration(
                    uniqueName = uniqueName,
                    displayName = displayName,
                    shortDescription = shortDescription,
                    rule = rule,
                    imageUrl = imageUrl,
                    minPlayers = minPlayers,
                    maxPlayers = maxPlayers,
                    frontEndUrl = frontEndUrl,
                    backEndUrl = backEndUrl
                ))
            }
        }

        with(newGameRegistration) {
            RegisteredGameEvent(
                id!!,
                uniqueName,
                displayName,
                shortDescription,
                rule,
                imageUrl,
                minPlayers,
                maxPlayers,
                frontEndUrl,
                backEndUrl
            )
        }
            .also { presenter.present(listOf(it)) }
    }

    data class Request(
        val uniqueName: String,
        val displayName: String,
        val shortDescription: String,
        val rule: String,
        val imageUrl: String,
        val minPlayers: Int,
        val maxPlayers: Int,
        val frontEndUrl: String,
        val backEndUrl: String
    )

    interface Presenter {
        fun present(events: List<DomainEvent>)
    }
}
