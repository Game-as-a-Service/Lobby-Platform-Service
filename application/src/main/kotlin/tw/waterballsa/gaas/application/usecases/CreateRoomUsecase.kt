package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.presenters.RoomPresenter
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.Room.Player
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.GAME_NOT_FOUND
import tw.waterballsa.gaas.exceptions.enums.PlatformError.PLAYER_JOIN_ROOM_ERROR
import javax.inject.Named

@Named
class CreateRoomUsecase(
    roomRepository: RoomRepository,
    userRepository: UserRepository,
    private val gameRegistrationRepository: GameRegistrationRepository,
    private val eventBus: EventBus,
) : AbstractRoomUseCase(roomRepository, userRepository) {
    fun execute(request: Request, presenter: RoomPresenter) {
        with(request) {
            val host = findPlayerByIdentity(userIdentity)
            host.ensureHostWouldNotCreatedRoomAgain()

            createRoom(host)
                .also { presenter.present(it) }
        }
    }

    private fun Player.ensureHostWouldNotCreatedRoomAgain() {
        if (roomRepository.existsByHostId(User.Id(id.value))) {
            throw PlatformException(PLAYER_JOIN_ROOM_ERROR, "A user can only create one room at a time.")
        }
    }

    private fun Request.createRoom(host: Player): Room {
        val gameRegistration = findGameRegistrationById(gameId)
        host.ready()
        return roomRepository.createRoom(toRoom(gameRegistration, host))
    }

    private fun findGameRegistrationById(gameId: String) =
        gameRegistrationRepository.findById(GameRegistration.Id(gameId))
            ?: throw notFound(GAME_NOT_FOUND, GameRegistration::class).id(gameId)

    data class Request(
        val name: String,
        val gameId: String,
        val userIdentity: String,
        val password: String? = null,
        val minPlayers: Int,
        val maxPlayers: Int,
    )
}

private fun CreateRoomUsecase.Request.toRoom(gameRegistration: GameRegistration, host: Player): Room =
    Room(
        name = name,
        game = gameRegistration,
        host = host,
        players = mutableListOf(host),
        password = password,
        minPlayers = minPlayers,
        maxPlayers = maxPlayers,
    )
