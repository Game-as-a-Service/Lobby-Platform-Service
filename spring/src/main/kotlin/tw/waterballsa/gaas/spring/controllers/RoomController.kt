package tw.waterballsa.gaas.spring.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tw.waterballsa.gaas.application.usecases.JoinRoomUsecase
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.events.JoinedRoomEvent
import tw.waterballsa.gaas.spring.extensions.getEvent

@RestController
@RequestMapping("/rooms")
class RoomController(
    private val joinRoomUsecase: JoinRoomUsecase
) {
    @PostMapping("/{roomId}/players")
    fun joinRoom(@PathVariable roomId: String, @RequestBody request: JoinRoomRequest): ResponseEntity<Any> {
        val presenter = JoinRoomPresenter()
        joinRoomUsecase.execute(request.toRequest(roomId), presenter)
        return presenter.viewModel
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.noContent().build()
    }

    class JoinRoomRequest(
        private val userId: String,
        private val password: String? = null,
    ) {
        fun toRequest(roomId: String): JoinRoomUsecase.Request =
            JoinRoomUsecase.Request(
                roomId = roomId,
                userId = userId,
                password = password
            )
    }

    class JoinRoomPresenter : JoinRoomUsecase.Presenter {
        var viewModel: JoinRoomViewModel? = null
            private set

        override fun present(vararg events: DomainEvent) {
            viewModel = events.getEvent(JoinedRoomEvent::class)?.toViewModel()
        }

        private fun JoinedRoomEvent.toViewModel(): JoinRoomViewModel =
            JoinRoomViewModel(
                id = id,
                name = name,
                status = status,
                gameRegistrationId = gameRegistrationId.value,
                hostId = host.id.value,
                hostName = hostName,
                playerIds = playerIds.map { it.id.value },
                maxPlayers = maxPlayers,
                minPlayers = minPlayers,
                isEncrypted = isEncrypted
            )
    }

    data class JoinRoomViewModel(
        val id: Room.Id,
        val name: String,
        val status: Room.Status,
        val gameRegistrationId: String,
        val hostId: String,
        val hostName: String,
        val playerIds: List<String>,
        val maxPlayers: Int,
        val minPlayers: Int,
        val isEncrypted: Boolean
    )
}