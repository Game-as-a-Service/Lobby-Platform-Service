package tw.waterballsa.gaas.spring.controllers

import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.*
import tw.waterballsa.gaas.application.usecases.CreateRoomUsecase
import tw.waterballsa.gaas.application.usecases.GetRoomsUseCase
import tw.waterballsa.gaas.application.usecases.Presenter
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.CreatedRoomEvent
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.spring.controllers.RoomController.CreateRoomViewModel
import tw.waterballsa.gaas.spring.extensions.getEvent
import javax.validation.Valid
import javax.validation.constraints.Pattern
import tw.waterballsa.gaas.application.usecases.JoinRoomUsecase
import tw.waterballsa.gaas.exceptions.PlatformException

@RestController
@RequestMapping("/rooms")
class RoomController(
    private val createRoomUsecase: CreateRoomUsecase,
    private val joinRoomUsecase: JoinRoomUsecase,
    private val getRoomsUseCase: GetRoomsUseCase
) {
    @PostMapping
    fun createRoom(
        @AuthenticationPrincipal principal: OidcUser,
        @RequestBody @Valid request: CreateRoomRequest
    ): ResponseEntity<Any> {
        val presenter = CreateRoomPresenter()
        createRoomUsecase.execute(request.toRequest(principal.subject), presenter)
        return presenter.viewModel
            ?.let { ResponseEntity.status(CREATED).body(it) }
            ?: ResponseEntity.noContent().build()
    }

    @PostMapping("/{roomId}/players")
    fun joinRoom(
        @AuthenticationPrincipal principal: OidcUser,
        @PathVariable roomId: String,
        @RequestBody request: JoinRoomRequest
    ): JoinRoomViewModel {
        val joinerId = principal.subject ?: throw PlatformException("User id must exist.")
        joinRoomUsecase.execute(request.toRequest(roomId, joinerId))
        return JoinRoomViewModel("success")
    }

    @GetMapping
    fun getRooms(@RequestBody request: GetRoomsRequest): GetRoomsViewModel {
        return getRoomsUseCase.execute(request.toRequest()).toRoomsViewModel(request)
    }

    class CreateRoomRequest(
        private val name: String,
        private val gameId: String,
        @field:Pattern(regexp = """^\d{4}$""", message = "The length must be 4 and can only contain digits.")
        private val password: String? = null,
        private val maxPlayers: Int,
        private val minPlayers: Int,
    ) {
        fun toRequest(hostId: String): CreateRoomUsecase.Request =
            CreateRoomUsecase.Request(
                gameId = gameId,
                hostId = hostId,
                maxPlayers = maxPlayers,
                minPlayers = minPlayers,
                name = name,
                password = password
            )
    }

    class CreateRoomPresenter : Presenter {
        var viewModel: CreateRoomViewModel? = null
            private set

        override fun present(vararg events: DomainEvent) {
            viewModel = events.getEvent(CreatedRoomEvent::class)?.toViewModel()
        }

        private fun CreatedRoomEvent.toViewModel(): CreateRoomViewModel =
            CreateRoomViewModel(
                id = roomId,
                game = game.toView(),
                host = host.toView(),
                currentPlayers = currentPlayers,
                maxPlayers = maxPlayers,
                minPlayers = minPlayers,
                name = name,
                isLocked = isLocked
            )
    }

    data class CreateRoomViewModel(
        val id: Room.Id,
        val name: String,
        val game: Game,
        val host: Player,
        val isLocked: Boolean,
        val currentPlayers: Int,
        val maxPlayers: Int,
        val minPlayers: Int,
    ) {
        data class Game(val id: String, val name: String)
        data class Player(val id: String, val nickname: String)
    }

    class JoinRoomRequest(
        val password: String? = null
    ) {
        fun toRequest(roomId: String, userId: String): JoinRoomUsecase.Request =
            JoinRoomUsecase.Request(
                roomId = roomId,
                userId = userId,
                password = password
            )
    }

    data class JoinRoomViewModel(
        val message: String
    )

    class GetRoomsRequest(
        val status: String,
        val page: Int,
        val perPage: Int
    ) {
        fun toRequest(): GetRoomsUseCase.Request =
            GetRoomsUseCase.Request(
                status = status,
                page = page,
                perPage = perPage
            )
    }

    data class GetRoomsViewModel(
        val rooms: List<RoomViewModel>,
        val meta: Meta
    ) {
        data class RoomViewModel(
            val id: String,
            val name: String,
            val game: Game,
            val host: Player,
            val maxPlayers: Int,
            val minPlayers: Int,
            val currentPlayers: Int,
            val isLocked: Boolean,
        ) {
            data class Game(val id: String, val name: String)
            data class Player(val id: String, val nickname: String)
        }

        data class Meta(
            val total: Int,
            val page: Int,
            val perPage: Int
        )
    }
}

private fun GameRegistration.toView(): CreateRoomViewModel.Game =
    CreateRoomViewModel.Game(id!!.value, displayName)

private fun Room.Player.toView(): CreateRoomViewModel.Player =
    CreateRoomViewModel.Player(id.value, nickname)

private fun List<Room>.toRoomsViewModel(request: RoomController.GetRoomsRequest): RoomController.GetRoomsViewModel =
    RoomController.GetRoomsViewModel(
        rooms = map { room ->
            RoomController.GetRoomsViewModel.RoomViewModel(
                id = room.roomId!!.value,
                name = room.name,
                game = room.game.toGetRoomsView(),
                host = room.host.toGetRoomsView(),
                minPlayers = room.minPlayers,
                maxPlayers = room.maxPlayers,
                currentPlayers = room.players.size,
                isLocked = room.isLocked,
            )
        }, meta = RoomController.GetRoomsViewModel.Meta(
            page = request.page,
            perPage = request.perPage,
            total = size
        )
    )

private fun GameRegistration.toGetRoomsView(): RoomController.GetRoomsViewModel.RoomViewModel.Game =
    RoomController.GetRoomsViewModel.RoomViewModel.Game(id!!.value, displayName)

private fun Room.Player.toGetRoomsView(): RoomController.GetRoomsViewModel.RoomViewModel.Player =
    RoomController.GetRoomsViewModel.RoomViewModel.Player(id.value, nickname)
