package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.exceptions.PlatformException
import javax.inject.Named

@Named
class CloseRoomUsecase(
    roomRepository: RoomRepository,
    userRepository: UserRepository,
) : AbstractRoomUseCase(roomRepository, userRepository) {
    fun execute(request: Request) {
        with(request) {
            val room = findRoomById(roomId)
            val host = findPlayerByIdentity(userIdentity)
            room.validateRoomHost(host.id)
            roomRepository.deleteById(room.roomId!!)
        }
    }

    private fun Room.validateRoomHost(userId: Room.Player.Id) {
        if (host.id != userId) {
            throw PlatformException("Player($userId) is not the host")
        }
    }

    data class Request(
        val roomId: String,
        val userIdentity: String,
    )
}
