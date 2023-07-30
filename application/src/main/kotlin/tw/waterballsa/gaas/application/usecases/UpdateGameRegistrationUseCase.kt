package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.events.GameRegistrationUpdatedEvent
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.GAME_EXISTS
import tw.waterballsa.gaas.exceptions.enums.PlatformError.GAME_NOT_FOUND
import javax.inject.Named

@Named
class UpdateGameRegistrationUseCase(
    private val gameRegistrationRepository: GameRegistrationRepository,
    private val eventBus: EventBus,
) {

    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            validateGameIdExist()
            validateUniqueNameDuplicated()
            val gameRegistration = updateGameRegistration()

            val event = gameRegistration.toGameRegistrationUpdatedEvent()
            presenter.present(event)
            eventBus.broadcast(event)
        }
    }

    private fun Request.validateGameIdExist() {
        gameRegistrationRepository.findById(gameId)
            ?: throw notFound(GAME_NOT_FOUND, GameRegistration::class).id(gameId)
    }

    private fun Request.validateUniqueNameDuplicated() {
        gameRegistrationRepository.findGameRegistrationByUniqueName(uniqueName)
            ?.takeIf { it.id != gameId }
            ?.let {
                throw PlatformException(
                    GAME_EXISTS,
                    "$uniqueName already exists",
                )
            }
    }

    private fun Request.updateGameRegistration(): GameRegistration =
        gameRegistrationRepository.updateGame(toGameRegistration())

    data class Request(
        val gameId: GameRegistration.Id,
        val uniqueName: String,
        val displayName: String,
        val shortDescription: String,
        val rule: String,
        val imageUrl: String,
        val minPlayers: Int,
        val maxPlayers: Int,
        val frontEndUrl: String,
        val backEndUrl: String,
    ) {
        fun toGameRegistration(): GameRegistration = GameRegistration(
            gameId,
            uniqueName,
            displayName,
            shortDescription,
            rule,
            imageUrl,
            minPlayers,
            maxPlayers,
            frontEndUrl,
            backEndUrl,
        )
    }

}

private fun GameRegistration.toGameRegistrationUpdatedEvent(): GameRegistrationUpdatedEvent =
    GameRegistrationUpdatedEvent(
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
    )
