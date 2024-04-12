package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.presenters.Presenter
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.events.RegisteredGameEvent
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.GAME_EXISTS
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Named

@Named
class RegisterGameUsecase(
    private val gameRegistrationRepository: GameRegistrationRepository,
    private val eventBus: EventBus,
) {
    fun execute(request: Request, presenter: Presenter) {
        gameRegistrationRepository.run {
            val uniqueName = request.uniqueName
            val gameRegistration = when {
                existsByUniqueName(uniqueName) -> throw PlatformException(
                    GAME_EXISTS,
                    "$uniqueName already exists",
                )

                else -> registerGame(request.toGameRegistration())
            }
            gameRegistration.toRegisteredGameEvent()
        }.also { event ->
            presenter.present(event)
            eventBus.broadcast(event)
        }
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
        val backEndUrl: String,
    )
}

private fun RegisterGameUsecase.Request.toGameRegistration(): GameRegistration =
    GameRegistration(
        uniqueName = uniqueName,
        displayName = displayName,
        shortDescription = shortDescription,
        rule = rule,
        imageUrl = imageUrl,
        minPlayers = minPlayers,
        maxPlayers = maxPlayers,
        frontEndUrl = frontEndUrl,
        backEndUrl = backEndUrl,
        createdOn = Instant.now().truncatedTo(ChronoUnit.DAYS),
    )

private fun GameRegistration.toRegisteredGameEvent(): RegisteredGameEvent =
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
        backEndUrl,
        createdOn,
    )
