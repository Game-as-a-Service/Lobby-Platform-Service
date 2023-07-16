package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.extension.toRoomPlayer
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.Room.Player
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.events.CreatedRoomEvent
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.PlatformException
import javax.inject.Named

@Named
class CreateRoomUsecase(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val gameRegistrationRepository: GameRegistrationRepository,
    private val eventBus: EventBus,
) {
    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            val hostPlayer = findPlayerByIdentity(hostIdentity)
            hostPlayer.ensureHostWouldNotCreatedRoomAgain()

            createRoom(hostPlayer)
                .toCreatedRoomEvent()
                .also { presenter.present(it) }
        }
    }

    private fun Player.ensureHostWouldNotCreatedRoomAgain() {
        if (roomRepository.existsByHostId(User.Id(id.value))) {
            throw PlatformException("A user can only create one room at a time.")
        }
    }

    private fun Request.createRoom(hostPlayer: Player): Room {
        val gameRegistration = findGameRegistrationById(gameId)
        return roomRepository.createRoom(toRoom(gameRegistration, hostPlayer))
    }

    private fun findGameRegistrationById(gameId: String) =
        gameRegistrationRepository.findById(GameRegistration.Id(gameId))
            ?: throw notFound(GameRegistration::class).id(gameId)

    private fun findPlayerByIdentity(identityProviderId: String): Player =
        userRepository.findByIdentity(identityProviderId)
            ?.toRoomPlayer()
            ?: throw notFound(User::class).message()

    data class Request(
        val name: String,
        val gameId: String,
        val hostIdentity: String,
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

private fun Room.toCreatedRoomEvent(): CreatedRoomEvent =
    CreatedRoomEvent(
        roomId = roomId!!,
        game = game,
        host = host,
        currentPlayers = players.size,
        maxPlayers = maxPlayers,
        minPlayers = minPlayers,
        name = name,
        isLocked = isLocked,
    )
