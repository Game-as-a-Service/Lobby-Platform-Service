package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.PlatformException
import javax.inject.Named

@Named
class JoinRoomUsecase(
    private val roomRepository: RoomRepository,
    userRepository: UserRepository,
    private val eventBus: EventBus,
) : AbstractRoomUseCase(userRepository) {
    fun execute(request: Request) {
        val (roomId, userIdentity, password) = request
        val room = findRoomById(Room.Id(roomId))
        val player = findPlayerByIdentity(userIdentity)
        validatePlayerJoinedRoom(player)
        room.run {
            validateRoomPassword(password)
            validateFullRoom()
            joinPlayer(player)
        }
    }

    private fun validatePlayerJoinedRoom(player: Room.Player) {
        val hasJoined = roomRepository.hasPlayerJoinedRoom(User.Id(player.id.value))
        if (hasJoined) {
            throw PlatformException("Player(${player.id.value}) has joined another room.")
        }
    }

    private fun findRoomById(roomId: Room.Id) =
        roomRepository.findById(roomId)
            ?: throw notFound(Room::class).id(roomId)

    private fun Room.validateRoomPassword(password: String?) {
        if (isLocked && !isPasswordCorrect(password)) {
            throw PlatformException("wrong password")
        }
    }

    private fun Room.validateFullRoom() {
        if (isFull()) {
            throw PlatformException("The room ($roomId) is full. Please select another room or try again later.")
        }
    }

    private fun Room.joinPlayer(player: Room.Player): Room {
        addPlayer(player)
        return roomRepository.update(this)
    }

    data class Request(
        val roomId: String,
        val userIdentity: String,
        val password: String? = null,
    )
}
