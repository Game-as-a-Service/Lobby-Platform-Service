package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.PlatformException
import javax.inject.Named

@Named
class CloseRoomUsecase(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
) {
    fun execute(request: Request) {
        with(request) {
            val room = findRoomById(Room.Id(roomId))
            val user = findUserByIdentity(userIdentity)
            room.validateRoomHost(Room.Player.Id(user.id!!.value))
            roomRepository.deleteById(Room.Id(roomId))
        }
    }

    private fun findRoomById(roomId: Room.Id) =
        roomRepository.findById(roomId)
            ?: throw notFound(Room::class).id(roomId)

    private fun findUserByIdentity(userIdentity: String) =
        userRepository.findByIdentity(userIdentity)
            ?: throw notFound(User::class).message()

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
