package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.extension.toRoomPlayer
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import javax.inject.Named

@Named
class KickPlayerUsecase(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
) {
    fun execute(request: Request) {
        with(request) {
            val room = roomRepository.findById(Room.Id(roomId)) ?: throw notFound(Room::class).message()
            val hostPlayer = userRepository.findByIdentity(hostIdentity)?.toRoomPlayer()
                ?: throw notFound(User::class).message()
            room.kickPlayer(hostPlayer.id, Room.Player.Id(playerId))
            roomRepository.update(room)
        }
    }

    data class Request(
        val roomId: String,
        val hostIdentity: String,
        val playerId: String,
    )
}
