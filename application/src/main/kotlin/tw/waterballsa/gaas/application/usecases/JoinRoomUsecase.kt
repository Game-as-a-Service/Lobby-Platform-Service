package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.events.JoinedRoomEvent
import tw.waterballsa.gaas.exceptions.WrongRoomPasswordException
import tw.waterballsa.gaas.exceptions.NotFoundException
import javax.inject.Named

@Named
class JoinRoomUsecase(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val eventBus: EventBus
){

    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            validateJoinRoom(this)
            joinRoom(request)
                .also { presenter.present(it.toJoinedRoomEvent("success"))}
        }
    }

    private fun Request.validateJoinRoom(request: Request) {
        val room = findRoomById(Room.Id(request.roomId))
        if(room.validatePassword(request.password)){
            throw WrongRoomPasswordException()
        }
    }

    private fun Request.joinRoom(request: Request): Room {
        val room = findRoomById(Room.Id(request.roomId))
        val player = findPlayerByUserId(User.Id(request.userId))
        room.players.add(player)
        return roomRepository.joinRoom(room)
    }

    private fun findRoomById(roomId: Room.Id) =
        roomRepository.findById(roomId)
            ?: throw NotFoundException("Room(${roomId.value}) not found.")
    private fun findPlayerByUserId(userId: User.Id) =
        userRepository.findById(userId)
            ?.let { Room.Player(Room.Player.Id(it.id!!.value), it.nickname) }
            ?: throw NotFoundException("User(${userId.value}) not found.")
    data class Request(
        val roomId: String,
        val userId: String,
        val password: String? = null,
    )

    interface Presenter {
        fun present(vararg events: DomainEvent)
    }
}

private fun Room.toJoinedRoomEvent(message: String): JoinedRoomEvent =
    JoinedRoomEvent(
       message = message
    )