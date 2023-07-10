package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.Room.Player
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import javax.inject.Named

@Named
class LeaveRoomUsecase(
    private val roomRepository: RoomRepository,
    private val eventBus: EventBus,
) {

    fun execute(request: LeaveRoomUsecase.Request) {
        with(request) {
            val room = findRoomById(Room.Id(roomId))
            room.leaveRoom(Player.Id(playerId))
            roomRepository.leaveRoom(room)
        }
    }

    private fun findRoomById(roomId: Room.Id) =
        roomRepository.findById(roomId)
            ?: throw notFound(Room::class).id(roomId)

    data class Request(
        val roomId: String,
        val playerId: String,
    )
}
