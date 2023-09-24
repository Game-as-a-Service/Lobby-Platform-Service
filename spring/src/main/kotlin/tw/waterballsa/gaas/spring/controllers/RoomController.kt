package tw.waterballsa.gaas.spring.controllers

import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import tw.waterballsa.gaas.application.usecases.*
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.JWT_ERROR
import tw.waterballsa.gaas.spring.controllers.presenter.*
import tw.waterballsa.gaas.spring.controllers.viewmodel.*
import javax.validation.Valid
import javax.validation.constraints.Pattern
import javax.validation.constraints.Positive

@RestController
class RoomController(
    private val createRoomUsecase: CreateRoomUsecase,
    private val joinRoomUsecase: JoinRoomUsecase,
    private val getRoomsUseCase: GetRoomsUseCase,
    private val closeRoomsUseCase: CloseRoomUsecase,
    private val changePlayerReadinessUsecase: ChangePlayerReadinessUsecase,
    private val kickPlayerUseCase: KickPlayerUsecase,
    private val leaveRoomUsecase: LeaveRoomUsecase,
    private val getRoomUsecase: GetRoomUsecase,
    private val startGameUseCase: StartGameUseCase,
    private val fastJoinRoomUseCase: FastJoinRoomUseCase,
) {
    @PostMapping("/rooms")
    fun createRoom(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody @Valid request: CreateRoomRequest
    ): ResponseEntity<Any> {
        val presenter = CreateRoomPresenter()
        createRoomUsecase.execute(request.toRequest(jwt.identityProviderId), presenter)
        return presenter.viewModel
            ?.let { ResponseEntity.status(CREATED).body(it) }
            ?: ResponseEntity.noContent().build()
    }

    @PostMapping("/rooms/{roomId}/players")
    fun joinRoom(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable roomId: String,
        @RequestBody request: JoinRoomRequest
    ): PlatformViewModel {
        joinRoomUsecase.execute(request.toRequest(roomId, jwt.identityProviderId))
        return PlatformViewModel.success()
    }

    @GetMapping("/rooms")
    fun getRooms(
        @RequestParam status: String,
        @RequestParam page: Int,
        @RequestParam perPage: Int
    ): GetRoomsViewModel {
        val request = GetRoomsRequest(status, page, perPage)
        val presenter = GetRoomsPresenter()
        getRoomsUseCase.execute(request.toRequest(), presenter)
        return presenter.viewModel
    }

    @PostMapping("/rooms/{roomId}/players/me:ready")
    fun readyForRoom(
        @PathVariable roomId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): PlatformViewModel {
        val request = ChangePlayerReadinessUsecase.Request.ready(roomId, jwt.identityProviderId)
        changePlayerReadinessUsecase.execute(request)
        return PlatformViewModel.success()
    }

    @PostMapping("/rooms/{roomId}/players/me:cancel")
    fun cancelReadyForRoom(
        @PathVariable roomId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): PlatformViewModel {
        val request = ChangePlayerReadinessUsecase.Request.cancelReady(roomId, jwt.identityProviderId)
        changePlayerReadinessUsecase.execute(request)
        return PlatformViewModel.success()
    }

    @DeleteMapping("/rooms/{roomId}")
    @ResponseStatus(NO_CONTENT)
    fun closeRoom(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable roomId: String,
    ) {
        val request = CloseRoomUsecase.Request(
            roomId = roomId,
            userIdentity = jwt.identityProviderId,
        )
        closeRoomsUseCase.execute(request)
    }

    @DeleteMapping("/rooms/{roomId}/players/{playerId}")
    fun kickPlayer(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable roomId: String,
        @PathVariable playerId: String,
    ): PlatformViewModel {
        val hostId = jwt.identityProviderId
        val request = KickPlayerUsecase.Request(roomId, hostId, playerId)
        kickPlayerUseCase.execute(request)
        return PlatformViewModel.success()
    }

    @DeleteMapping("/rooms/{roomId}/players/me")
    @ResponseStatus(NO_CONTENT)
    fun leaveRoom(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable roomId: String
    ) {
        leaveRoomUsecase.execute(LeaveRoomUsecase.Request(roomId, jwt.identityProviderId))
    }

    @GetMapping("/rooms/{roomId}")
    fun getRoom(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable roomId: String,
    ): GetRoomViewModel {
        val request = GetRoomUsecase.Request(roomId, jwt.identityProviderId)
        val presenter = GetRoomPresenter()
        getRoomUsecase.execute(request, presenter)
        return presenter.viewModel
    }

    @PostMapping("/rooms/{roomId}:startGame")
    fun startGame(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable roomId: String,
    ): StartGameViewModel {
        val request = StartGameUseCase.Request(roomId, jwt.tokenValue, jwt.identityProviderId)
        val presenter = StartGamePresenter()
        startGameUseCase.execute(request, presenter)
        return presenter.viewModel
    }

    @PostMapping("/rooms:fastJoin")
    fun fastJoinRoom(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: FastJoinRoomRequest,
    ): FastJoinRoomViewModel {
        val fastJoinRequest = FastJoinRoomUseCase.Request(request.gameId, jwt.identityProviderId)
        val presenter = FastJoinRoomPresenter()
        fastJoinRoomUseCase.execute(fastJoinRequest, presenter)
        return presenter.viewModel
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
                userIdentity = hostId,
                maxPlayers = maxPlayers,
                minPlayers = minPlayers,
                name = name,
                password = password
            )
    }

    class JoinRoomRequest(
        val password: String? = null
    ) {
        fun toRequest(roomId: String, userId: String): JoinRoomUsecase.Request =
            JoinRoomUsecase.Request(
                roomId = roomId,
                userIdentity = userId,
                password = password
            )
    }

    class FastJoinRoomRequest(
        val gameId: String
    )

    class GetRoomsRequest(
        @field:Pattern(
            regexp = """^(WAITING|PLAYING)$""",
            message = "The status must be either WAITING or PLAYING."
        )
        val status: String,
        @field:Positive(message = "The page must be a positive number.")
        val page: Int,
        @field:Positive(message = "The perPage must be a positive number.")
        val perPage: Int
    ) {
        fun toRequest(): GetRoomsUseCase.Request =
            GetRoomsUseCase.Request(
                status = Room.Status.valueOf(status),
                page = page,
                offset = perPage
            )
    }
}

private val Jwt.identityProviderId: String
    get() = subject ?: throw PlatformException(JWT_ERROR, "identityProviderId should exist.")


