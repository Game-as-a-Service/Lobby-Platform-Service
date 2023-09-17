package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.client.GameService
import tw.waterballsa.gaas.application.client.StartGameRequest
import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.GAME_START_FAILED
import javax.inject.Named

@Named
class StartGameUseCase(
    roomRepository: RoomRepository,
    userRepository: UserRepository,
    private val gameService: GameService,
    private val eventBus: EventBus
) : AbstractRoomUseCase(roomRepository, userRepository) {

    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            val room = findRoomById(roomId)
            val hostPlayer = findPlayerByIdentity(identityProviderId)

            room.run {
                validateRoomHost(hostPlayer.id)
                validatePlayersGreaterThanMinLimit()
                validateAllPlayersReady()
            }

            val gameServerUrl = room.startGameByHost(jwtToken)

            room.startGame()
            roomRepository.update(room)
            presenter.present(gameServerUrl)
        }
    }

    data class Request(
        val roomId: String,
        val jwtToken: String,
        val identityProviderId: String
    )

    interface Presenter {
        fun present(url: String)
    }

    private fun Room.validatePlayersGreaterThanMinLimit() {
        if (players.size < game.minPlayers) {
            throw PlatformException(GAME_START_FAILED, "Not enough players")
        }
    }

    private fun Room.validateAllPlayersReady() {
        if (!players.all { it.readiness }) {
            throw PlatformException(GAME_START_FAILED, "Not all players are ready")
        }
    }

    private fun Room.startGameByHost(jwtToken: String): String {
        val gameServerHost = game.backEndUrl
        val startGameRequest = StartGameRequest(players.map { it.toGamePlayer() })

        return gameService.startGame(gameServerHost, jwtToken, startGameRequest).url
    }

    private fun Room.Player.toGamePlayer(): StartGameRequest.GamePlayer =
        StartGameRequest.GamePlayer(id.value, nickname)
}
