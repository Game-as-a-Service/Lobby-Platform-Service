package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.extension.toRoomPlayer
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound

abstract class AbstractRoomUseCase(
    protected val roomRepository: RoomRepository,
    protected val userRepository: UserRepository,
) {
    protected fun findPlayerByIdentity(userIdentity: String): Room.Player =
        userRepository.findByIdentity(userIdentity)
            ?.toRoomPlayer()
            ?: throw notFound(User::class).message()

    protected fun findRoomById(roomId: String, messageWithId: Boolean = true): Room {
        val id = Room.Id(roomId)
        val notFoundException = if (messageWithId) {
            notFound(Room::class).id(id)
        } else {
            notFound(Room::class).message()
        }
        return roomRepository.findById(id)
            ?: throw notFoundException
    }
}
