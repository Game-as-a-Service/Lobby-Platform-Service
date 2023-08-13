package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import javax.inject.Named

@Named
class LeaveRoomUsecase(
    roomRepository: RoomRepository,
    userRepository: UserRepository,
    private val eventBus: EventBus,
) : AbstractRoomUseCase(roomRepository, userRepository) {
    fun execute(request: Request) {
        with(request) {
            val room = findRoomById(roomId)
            val player = findPlayerByIdentity(userIdentity)
            room.leaveRoom(player.id)
            roomRepository.leaveRoom(room)
            if (room.isEmpty()) roomRepository.deleteById(room.roomId!!)
        }
    }

    data class Request(
        val roomId: String,
        val userIdentity: String,
    )
}
