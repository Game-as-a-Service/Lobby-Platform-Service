package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.ChangePlayerReadinessEvent
import tw.waterballsa.gaas.events.ChangePlayerReadinessEvent.Data
import tw.waterballsa.gaas.events.ChangePlayerReadinessEvent.Data.User
import tw.waterballsa.gaas.events.enums.EventMessageType.USER_READY
import tw.waterballsa.gaas.events.enums.EventMessageType.USER_NOT_READY
import javax.inject.Named

@Named
class ChangePlayerReadinessUsecase(
    roomRepository: RoomRepository,
    userRepository: UserRepository,
    private val eventBus: EventBus,
) : AbstractRoomUseCase(roomRepository, userRepository) {
    fun execute(request: Request) {
        with(request) {
            val room = findRoomById(roomId, false)
            val player = findPlayerByIdentity(userIdentity)
            room.changePlayerReadiness(player.id, readiness)
            roomRepository.update(room)

            val changePlayerReadinessEvent = room.toChangePlayerReadinessEvent(readiness, player.id!!.value, player.nickname)
            eventBus.broadcast(changePlayerReadinessEvent)
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

fun Room.toChangePlayerReadinessEvent(
    readiness: Boolean,
    playerId: String,
    nickname: String
): ChangePlayerReadinessEvent {
    val type = if (readiness) USER_READY else USER_NOT_READY
    return ChangePlayerReadinessEvent(
        type, Data(
            User(
                playerId,
                nickname
            ), roomId!!.value
        )
    )
}
