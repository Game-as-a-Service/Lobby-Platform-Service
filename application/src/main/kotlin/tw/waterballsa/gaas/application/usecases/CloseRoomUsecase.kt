package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.PlatformException
import javax.inject.Named

@Named
class CloseRoomUsecase(private val roomRepository: RoomRepository) {
    fun execute(request: Request) {
        with(request) {
            val room = findRoomById(Room.Id(roomId))
            room.validateRoomHost(Room.Player.Id(userId))
            roomRepository.deleteById(Room.Id(roomId))
        }
    }

    private fun findRoomById(roomId: Room.Id) =
        roomRepository.findById(roomId)
            ?: throw notFound(Room::class).id(roomId)

    private fun Room.validateRoomHost(userId: Room.Player.Id) {
        if (host.id != userId) {
            throw PlatformException("Player($userId) is not the host")
        }
    }

    data class Request(
        val roomId: String,
        val userId: String,
    )
}
