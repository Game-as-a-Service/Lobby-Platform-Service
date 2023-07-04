package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import javax.inject.Named

@Named
class KickPlayerUsecase(
    private val roomRepository: RoomRepository,
) {
    fun execute(request: Request) {
        with(request) {
            val room = roomRepository.findById(Room.Id(roomId)) ?: throw notFound(Room::class).message()
            room.kickPlayer(Room.Player.Id(hostId), Room.Player.Id(playerId))
            roomRepository.update(room)
        }
    }

    data class Request(
        val roomId: String,
        val hostId: String,
        val playerId: String
    )
}
