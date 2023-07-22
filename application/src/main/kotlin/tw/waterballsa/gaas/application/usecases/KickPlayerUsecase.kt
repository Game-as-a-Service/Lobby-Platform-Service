package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import javax.inject.Named

@Named
class KickPlayerUsecase(
    roomRepository: RoomRepository,
    userRepository: UserRepository,
) : AbstractRoomUseCase(roomRepository, userRepository) {
    fun execute(request: Request) {
        with(request) {
            val room = findRoomById(roomId, false)
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
