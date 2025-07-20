package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.client.GameService
import tw.waterballsa.gaas.application.client.StartGameRequest
import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.StartedGameEvent
import tw.waterballsa.gaas.events.StartedGameEvent.Data
import tw.waterballsa.gaas.events.enums.EventMessageType.GAME_STARTED
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.GAME_START_FAILED
import javax.inject.Named

@Named
class StartGameUseCase(
    roomRepository: RoomRepository,
    userRepository: UserRepository,
    private val gameService: GameService,
    private val eventBus: EventBus,
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

            val startedGameEvent = room.startGameByHost(jwtToken)

            room.startGame()
            roomRepository.update(room)
            presenter.present(startedGameEvent.data.gameUrl)
            eventBus.broadcast(startedGameEvent)
        }
    }

    data class Request(
        val roomId: String,
        val jwtToken: String,
        val identityProviderId: String,
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

    private fun Room.startGameByHost(jwtToken: String): StartedGameEvent {
        if (roomId == null) {
            throw PlatformException(GAME_START_FAILED, "Room Id is null")
        }
        val gameServerHost = game.backEndUrl
        val startGameRequest = StartGameRequest(roomId!!.value, players.map { it.toGamePlayer() })
        val startGameResponse = gameService.startGame(gameServerHost, jwtToken, startGameRequest)
        gameUrl = startGameResponse.url
        return StartedGameEvent(
            GAME_STARTED,
            Data(
                startGameResponse.url,
                roomId!!,
                game.id!!,
                players.map {
                    Data.Player(
                        it.id.value,
                        it.nickname,
                    )
                },
            ),
        )
    }

    private fun Room.Player.toGamePlayer(): StartGameRequest.GamePlayer =
        StartGameRequest.GamePlayer(id.value, nickname, nickname)
}
