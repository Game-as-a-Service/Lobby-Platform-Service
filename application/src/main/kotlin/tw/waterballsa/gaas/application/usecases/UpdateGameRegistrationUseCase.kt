package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.presenters.Presenter
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
            val gameRegistration = findGame(gameId)
                .validateUniqueNameDuplicated(request)
                .setValueByRequest(this)
                .updateGame()
            val event = gameRegistration.toGameRegistrationUpdatedEvent()
            presenter.present(event)
            eventBus.broadcast(event)
        }
    }

    private fun findGame(gameId: GameRegistration.Id): GameRegistration {
        return gameRegistrationRepository.findById(gameId)
            ?: throw notFound(GAME_NOT_FOUND, GameRegistration::class).id(gameId)
    }

    private fun GameRegistration.validateUniqueNameDuplicated(request: Request): GameRegistration {
        gameRegistrationRepository.findGameRegistrationByUniqueName(request.uniqueName)
            ?.takeIf { it.id != request.gameId }
            ?.let {
                throw PlatformException(
                    GAME_EXISTS,
                    "${request.uniqueName} already exists",
                )
            }
        return this
    }

    private fun GameRegistration.setValueByRequest(request: Request): GameRegistration {
        uniqueName = request.uniqueName
        displayName = request.displayName
        shortDescription = request.shortDescription
        rule = request.rule
        imageUrl = request.imageUrl
        minPlayers = request.minPlayers
        maxPlayers = request.maxPlayers
        frontEndUrl = request.frontEndUrl
        backEndUrl = request.backEndUrl
        return this
    }

    private fun GameRegistration.updateGame(): GameRegistration {
        return gameRegistrationRepository.updateGame(this)
    }

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
    )
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
        createdOn,
    )
