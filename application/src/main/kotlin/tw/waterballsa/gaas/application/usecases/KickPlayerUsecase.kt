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
            val room = roomRepository.findById(roomId) ?: throw notFound(Room::class).message()
            room.kickPlayer(hostId, playerId)
            roomRepository.update(room)
        }
    }

    data class Request(
        val roomId: Room.Id,
        val hostId: Room.Player.Id,
        val playerId: Room.Player.Id
    ) {
        companion object {
            fun toRequest(roomId: String, hostId: String, playerId: String) = Request(
                Room.Id(roomId),
                Room.Player.Id(hostId),
                Room.Player.Id(playerId)
            )
        }
    }
}
