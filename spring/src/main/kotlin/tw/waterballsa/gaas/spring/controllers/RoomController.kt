package tw.waterballsa.gaas.spring.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.*
import tw.waterballsa.gaas.application.usecases.JoinRoomUsecase
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.events.JoinedRoomEvent
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.spring.extensions.getEvent

@RestController
@RequestMapping("/rooms")
class RoomController(
    private val joinRoomUsecase: JoinRoomUsecase
) {
    @PostMapping("/{roomId}/players")
    fun joinRoom(@PathVariable roomId: String, @RequestBody request: JoinRoomRequest, @AuthenticationPrincipal principal: OidcUser?): ResponseEntity<Any> {
        try {
            val presenter = JoinRoomPresenter()
            val joinerId = principal?.subject ?: throw PlatformException("User id is null")
            joinRoomUsecase.execute(request.toRequest(roomId, joinerId), presenter)
            return presenter.viewModel
                ?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.noContent().build()
        }catch (e : Exception){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JoinRoomViewModel(e.message!!))
        }
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

    class JoinRoomPresenter : JoinRoomUsecase.Presenter {
        var viewModel: JoinRoomViewModel? = null
            private set

        override fun present(vararg events: DomainEvent) {
            viewModel = events.getEvent(JoinedRoomEvent::class)?.toViewModel()
        }

        private fun JoinedRoomEvent.toViewModel(): JoinRoomViewModel =
            JoinRoomViewModel(
                message = message
            )
    }

    data class JoinRoomViewModel(
        val message: String
    )
}