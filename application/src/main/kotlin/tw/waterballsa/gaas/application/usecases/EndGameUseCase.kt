package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.EndedGameEvent
import tw.waterballsa.gaas.events.enums.EventMessageType.GAME_ENDED
import javax.inject.Named

@Named
class EndGameUseCase(
    roomRepository: RoomRepository,
    userRepository: UserRepository,
    private val eventBus: EventBus,
) : AbstractRoomUseCase(roomRepository, userRepository) {
    fun execute(request: Request) {
        val room = findRoomById(request.roomId)
        room.endGame()
        roomRepository.update(room)

        val endedGameEvent = room.endGameByGameService()
        eventBus.broadcast(endedGameEvent)
    }

    data class Request(
        val roomId: String,
    )
}

fun Room.endGameByGameService(): EndedGameEvent {
    val type = GAME_ENDED
    val data = EndedGameEvent.Data(roomId!!.value)
    return EndedGameEvent(type, data)
}
