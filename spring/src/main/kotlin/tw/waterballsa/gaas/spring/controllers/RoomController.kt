package tw.waterballsa.gaas.spring.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.waterballsa.gaas.application.usecases.CreateRoomUsecase
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.CreatedRoomEvent
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.spring.extensions.getEvent

@RestController
@RequestMapping("/rooms")
class RoomController(
    private val createRoomUsecase: CreateRoomUsecase
) {
    @PostMapping
    fun createRoom(@RequestBody request: CreateRoomRequest): ResponseEntity<Any> {
        // TODO: Should validate user by principal in the future
        val presenter = CreateRoomPresenter()
        createRoomUsecase.execute(request.toRequest(), presenter)
        return presenter.viewModel
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.noContent().build()
    }

    class CreateRoomRequest(
        private val gameRegistrationId: String,
        private val hostId: String,
        private val maxPlayers: Int,
        private val minPlayers: Int,
        private val name: String,
        private val description: String,
        private val password: String? = null
    ) {
        fun toRequest(): CreateRoomUsecase.Request =
            CreateRoomUsecase.Request(
                gameRegistrationId = gameRegistrationId,
                hostId = hostId,
                maxPlayers = maxPlayers,
                minPlayers = minPlayers,
                name = name,
                description = description,
                password = password
            )
    }

    class CreateRoomPresenter : CreateRoomUsecase.Presenter {
        var viewModel: CreateRoomViewModel? = null
            private set

        override fun present(vararg events: DomainEvent) {
            viewModel = events.getEvent(CreatedRoomEvent::class)?.toViewModel()
        }

        private fun CreatedRoomEvent.toViewModel(): CreateRoomViewModel =
            CreateRoomViewModel(
                roomId = roomId,
                gameRegistrationId = gameRegistrationId.value,
                hostId = host.id.value,
                hostName = hostName,
                playerIds = playerIds.map { it.id.value },
                maxPlayers = maxPlayers,
                minPlayers = minPlayers,
                name = name,
                description = description,
                status = status,
                isEncrypted = isEncrypted
            )
    }

    data class CreateRoomViewModel(
        val roomId: Room.Id,
        val gameRegistrationId: String,
        val hostId: String,
        val hostName: String,
        val playerIds: List<String>,
        val maxPlayers: Int,
        val minPlayers: Int,
        val name: String,
        val description: String,
        val status: Room.Status,
        val isEncrypted: Boolean
    )
}
