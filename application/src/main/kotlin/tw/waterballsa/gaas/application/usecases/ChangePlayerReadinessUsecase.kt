package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import javax.inject.Named

@Named
class ChangePlayerReadinessUsecase(
    roomRepository: RoomRepository,
    userRepository: UserRepository,
) : AbstractRoomUseCase(roomRepository, userRepository) {
    fun execute(request: Request) {
        with(request) {
            val room = findRoomById(roomId, false)
            val player = findPlayerByIdentity(userIdentity)
            room.changePlayerReadiness(player.id, readiness)
            roomRepository.update(room)
        }
    }

    data class Request(
        val roomId: String,
        val userIdentity: String,
        val readiness: Boolean,
    ) {
        companion object {
            fun ready(roomId: String, userIdentity: String): Request = Request(roomId, userIdentity, true)

            fun cancelReady(roomId: String, userIdentity: String): Request = Request(roomId, userIdentity, false)
        }
    }
}
