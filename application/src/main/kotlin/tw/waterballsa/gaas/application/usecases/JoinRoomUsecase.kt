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
    private val eventBus: EventBus
){

    fun execute(request: Request) {
        with(request) {
            val room = findRoomById(Room.Id(roomId))
            room.validateRoomPassword(password)
            room.joinPlayer(userId)
        }
    }

    private fun findRoomById(roomId: Room.Id) =
        roomRepository.findById(roomId)
            ?: throw notFound(Room::class).id(roomId)

    private fun Room.validateRoomPassword(password: String?) {
        if(isLocked && !isPasswordCorrect(password)){
            throw PlatformException("wrong password")
        }
    }

    private fun Room.joinPlayer(userId: String): Room {
        val player = findPlayerByUserId(User.Id(userId))
        addPlayer(player)
        return roomRepository.joinRoom(this)
    }

    private fun findPlayerByUserId(userId: User.Id) =
        userRepository.findById(userId)
            ?.toRoomPlayer()
            ?: throw notFound(User::class).id(userId.value)

    data class Request(
        val roomId: String,
        val userId: String,
        val password: String? = null,
    )
}