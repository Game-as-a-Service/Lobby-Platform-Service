package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import javax.inject.Named

@Named
class LeaveRoomUsecase(
    private val roomRepository: RoomRepository,
    userRepository: UserRepository,
    private val eventBus: EventBus,
) : AbstractRoomUseCase(userRepository) {
    fun execute(request: Request) {
        with(request) {
            val room = findRoomById(Room.Id(roomId))
            val player = findPlayerByIdentity(userIdentity)
            room.leaveRoom(player.id)
            roomRepository.leaveRoom(room)
        }
    }

    private fun findRoomById(roomId: Room.Id) =
        roomRepository.findById(roomId)
            ?: throw notFound(Room::class).id(roomId)

    data class Request(
        val roomId: String,
        val userIdentity: String,
    )
}
