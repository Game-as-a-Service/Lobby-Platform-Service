package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.PlayerLeavedRoomEvent
import tw.waterballsa.gaas.events.PlayerLeavedRoomEvent.Data
import tw.waterballsa.gaas.events.PlayerLeavedRoomEvent.Data.Player
import tw.waterballsa.gaas.events.enums.EventMessageType.USER_LEFT
import javax.inject.Named

@Named
class LeaveRoomUsecase(
    roomRepository: RoomRepository,
    userRepository: UserRepository,
    private val eventBus: EventBus,
) : AbstractRoomUseCase(roomRepository, userRepository) {
    fun execute(request: Request) {
        with(request) {
            val room = findRoomById(roomId)
            val player = findPlayerByIdentity(userIdentity)
            room.leaveRoom(player.id)

            when {
                room.isEmpty() -> roomRepository.closeRoom(room)
                else -> roomRepository.leaveRoom(room)
            }

            val playerLeavedRoomEvent = room.leaveRoomEvent(player.id.value, player.nickname)
            eventBus.broadcast(playerLeavedRoomEvent)
        }
    }

    private fun Room.leaveRoomEvent(
        playerId: String,
        nickname: String,
    ): PlayerLeavedRoomEvent {
        val user = Player(playerId, nickname)
        val data = Data(user, roomId!!.value)
        return PlayerLeavedRoomEvent(USER_LEFT, data)
    }

    data class Request(
        val roomId: String,
        val userIdentity: String,
    )
}
