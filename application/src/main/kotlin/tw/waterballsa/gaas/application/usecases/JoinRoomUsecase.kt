package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.events.JoinedRoomEvent
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.JoinRoomException
import javax.inject.Named

@Named
class JoinRoomUsecase(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val eventBus: EventBus
){

    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            val room = findRoomById(Room.Id(this.roomId))
            validateRoomPassword(room)
            joinRoom(room)
                .also { presenter.present(it.toJoinedRoomEvent("success"))}
        }
    }

    private fun Request.validateRoomPassword(room: Room) {
        if(room.isPasswordCorrect(this.password)){
            throw JoinRoomException("wrong password")
        }
    }

    private fun Request.joinRoom(room: Room): Room {
        val player = findPlayerByUserId(User.Id(this.userId))
        room.addPlayer(player)
        return roomRepository.joinRoom(room)
    }

    private fun findRoomById(roomId: Room.Id) =
        roomRepository.findById(roomId)
            ?: throw notFound(Room::class).id(roomId)

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

private fun Room.toJoinedRoomEvent(message: String): JoinedRoomEvent =
    JoinedRoomEvent(
       message = message
    )