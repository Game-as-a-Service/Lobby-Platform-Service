package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import javax.inject.Named

@Named
class KickPlayerUsecase(
    private val roomRepository: RoomRepository,
    userRepository: UserRepository,
) : AbstractRoomUseCase(userRepository) {
    fun execute(request: Request) {
        with(request) {
            val room = roomRepository.findById(Room.Id(roomId)) ?: throw notFound(Room::class).message()
            val host = findPlayerByIdentity(userIdentity)
            room.kickPlayer(host.id, Room.Player.Id(playerId))
            roomRepository.update(room)
        }
    }

    data class Request(
        val roomId: String,
        val userIdentity: String,
        val playerId: String,
    )
}
