package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import javax.inject.Named

@Named
class ChangePlayerReadinessUsecase(
    private val roomRepository: RoomRepository,
    userRepository: UserRepository,
) : AbstractRoomUseCase(userRepository) {
    fun execute(request: Request) {
        with(request) {
            val room = roomRepository.findById(roomId.toRoomId())
                ?: throw notFound(Room::class).message()
            val player = findPlayerByIdentity(userIdentity)
            room.changePlayerReadiness(player.id, readiness)
            roomRepository.update(room)
        }
    }

    private fun String.toRoomId(): Room.Id = Room.Id(this)

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
