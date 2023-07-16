package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.extension.toRoomPlayer
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import javax.inject.Named

@Named
class ChangePlayerReadinessUsecase(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
) {
    fun execute(request: Request) {
        with(request) {
            val room = roomRepository.findById(roomId.toRoomId())
                ?: throw notFound(Room::class).message()
            val player = userRepository.findByIdentity(playerIdentity)?.toRoomPlayer()
                ?: throw notFound(User::class).message()
            room.changePlayerReadiness(player.id, readiness)
            roomRepository.update(room)
        }
    }

    private fun String.toRoomId(): Room.Id = Room.Id(this)

    data class Request(
        val roomId: String,
        val playerIdentity: String,
        val readiness: Boolean,
    ) {
        companion object {
            fun ready(roomId: String, userId: String): Request = Request(roomId, userId, true)

            fun cancelReady(roomId: String, userId: String): Request = Request(roomId, userId, false)
        }
    }
}
