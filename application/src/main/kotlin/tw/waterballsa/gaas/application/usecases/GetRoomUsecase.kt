package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.Room.Player
import tw.waterballsa.gaas.exceptions.ForbiddenException
import tw.waterballsa.gaas.exceptions.ForbiddenException.Companion.Message
import tw.waterballsa.gaas.exceptions.NotFoundException
import javax.inject.Named

@Named
class GetRoomUsecase(
    private val roomRepository: RoomRepository,
    private val eventBus: EventBus,
) {
    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            val room = findRoomById(Room.Id(roomId))!!
            room.validatePlayerInRoom(Player.Id(userId))
            room.also { presenter.present(it) }
        }
    }

    private fun findRoomById(roomId: Room.Id) =
        roomRepository.findById(roomId)
            ?: throw NotFoundException.notFound(Room::class).id(roomId)

    private fun Room.validatePlayerInRoom(playerId: Player.Id) {
        if (!hasPlayer(playerId)) {
            throw ForbiddenException.forbidden(Message.NOT_IN_ROOM).message()
        }
    }

    data class Request(
        val roomId: String,
        val userId: String
    )

    interface Presenter {
        fun present(room: Room)
    }
}