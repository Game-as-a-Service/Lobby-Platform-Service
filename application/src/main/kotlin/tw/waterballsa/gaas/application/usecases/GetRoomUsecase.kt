package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.Room.Player
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.PLAYER_NOT_IN_ROOM_ERROR
import javax.inject.Named

@Named
class GetRoomUsecase(
    roomRepository: RoomRepository,
    userRepository: UserRepository,
    private val eventBus: EventBus,
) : AbstractRoomUseCase(roomRepository, userRepository) {
    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            val room = findRoomById(roomId)
            val player = findPlayerByIdentity(userIdentity)
            room.validatePlayerInRoom(player.id)
            presenter.present(room)
        }
    }

    private fun Room.validatePlayerInRoom(playerId: Player.Id) {
        if (!hasPlayer(playerId)) {
            throw PlatformException(PLAYER_NOT_IN_ROOM_ERROR, "Player(${playerId.value}) is not in the room(${roomId!!.value}).")
        }
    }

    data class Request(
        val roomId: String,
        val userIdentity: String,
    )

    interface Presenter {
        fun present(room: Room)
    }
}
