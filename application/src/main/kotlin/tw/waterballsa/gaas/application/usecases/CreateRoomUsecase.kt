package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.exceptions.DuplicateRoomException
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.Room.Player
import tw.waterballsa.gaas.domain.User.UserId
import tw.waterballsa.gaas.events.CreatedRoomEvent
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.exceptions.NotFoundException
import javax.inject.Named

@Named
class CreateRoomUsecase(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val gameRegistrationRepository: GameRegistrationRepository,
    private val eventBus: EventBus
) {
    fun execute(request: Request, presenter: Presenter) {
        request.run {
            validateCreatedRoom(this)
            createRoom(this)
                .also { presenter.present(it.toCreatedRoomEvent()) }
        }
    }

    private fun validateCreatedRoom(request: Request) {
        if (roomRepository.existsByHostId(UserId(request.hostId))) {
            throw DuplicateRoomException("A user can only create one room at a time.")
        }
    }

    private fun createRoom(request: Request): Room {
        val gameRegistration = findGameRegistrationById(request.gameRegistrationId)
        val hostPlayer = findPlayerByUserId(UserId(request.hostId))
        return roomRepository.createRoom(request.toRoom(gameRegistration, hostPlayer))
    }

    private fun findGameRegistrationById(gameId: String) =
        gameRegistrationRepository.findGameRegistrationById(GameRegistration.GameRegistrationId(gameId))
            ?: throw NotFoundException("Game($gameId) not found.")

    private fun findPlayerByUserId(hostId: UserId): Player =
        userRepository.findUserById(hostId)
            ?.let { Player(it.id!!, it.nickname) }
            ?: throw NotFoundException("User(${hostId.value}) not found.")

    data class Request(
        val gameRegistrationId: String,
        val hostId: String,
        val maxPlayers: Int,
        val minPlayers: Int,
        val name: String,
        val description: String,
        val password: String? = null
    )

    interface Presenter {
        fun present(vararg events: DomainEvent)
    }
}

private fun CreateRoomUsecase.Request.toRoom(gameRegistration: GameRegistration, host: Player): Room =
    Room(
        host = host,
        gameRegistration = gameRegistration,
        players = listOf(host),
        maxPlayers = maxPlayers,
        minPlayers = minPlayers,
        name = name,
        description = description,
        password = password
    )

private fun Room.toCreatedRoomEvent(): CreatedRoomEvent =
    CreatedRoomEvent(
        roomId = roomId!!,
        gameRegistrationId = gameRegistration.id!!,
        host = host,
        hostName = host.nickname,
        playerIds = players,
        maxPlayers = maxPlayers,
        minPlayers = minPlayers,
        name = name,
        description = description,
        status = status,
        isEncrypted = !password.isNullOrEmpty()
    )
