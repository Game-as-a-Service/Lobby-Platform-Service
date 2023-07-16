package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.extension.toRoomPlayer
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
    private val userRepository: UserRepository,
    private val eventBus: EventBus,
) {

    fun execute(request: Request) {
        val (roomId, userIdentity, password) = request
        val room = findRoomById(Room.Id(roomId))
        val user = findUserByIdentity(userIdentity)
        validateUserJoinedRoom(user.id!!)
        room.run {
            validateRoomPassword(password)
            validateFullRoom()
            joinPlayer(user)
        }
    }

    private fun validateUserJoinedRoom(userId: User.Id) {
        val hasJoined = roomRepository.hasPlayerJoinedRoom(userId)
        if (hasJoined) {
            throw PlatformException("Player(${userId.value}) has joined another room.")
        }
    }

    private fun findRoomById(roomId: Room.Id) =
        roomRepository.findById(roomId)
            ?: throw notFound(Room::class).id(roomId)

    private fun findUserByIdentity(identityProviderId: String) =
        userRepository.findByIdentity(identityProviderId)
            ?: throw notFound(User::class).message()

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

    private fun Room.joinPlayer(user: User): Room {
        addPlayer(user.toRoomPlayer())
        return roomRepository.update(this)
    }

    data class Request(
        val roomId: String,
        val userIdentity: String,
        val password: String? = null,
    )
}
