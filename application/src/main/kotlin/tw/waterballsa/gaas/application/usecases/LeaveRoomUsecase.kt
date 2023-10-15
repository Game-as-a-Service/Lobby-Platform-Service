package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.UserLeavedRoomEvent
import tw.waterballsa.gaas.events.UserLeavedRoomEvent.Data
import tw.waterballsa.gaas.events.UserLeavedRoomEvent.Data.UserInfo
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

            val leaveRoomEvent = room.leaveRoomEvent(player.id.value, player.nickname)
            eventBus.broadcast(leaveRoomEvent)
        }
    }

    private fun Room.leaveRoomEvent(
        playerId: String,
        nickname: String
    ) : UserLeavedRoomEvent {
        val user = UserInfo(playerId, nickname)
        val data = Data(user, roomId!!.value)
        return UserLeavedRoomEvent(USER_LEFT, data)
    }

    data class Request(
        val roomId: String,
        val userIdentity: String,
    )
}
