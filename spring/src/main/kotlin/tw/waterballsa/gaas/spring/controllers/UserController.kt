package tw.waterballsa.gaas.spring.controllers

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.*
import tw.waterballsa.gaas.application.usecases.GetUserUseCase
import tw.waterballsa.gaas.application.usecases.Presenter
import tw.waterballsa.gaas.application.usecases.UpdateUserUseCase
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.events.UserUpdatedEvent
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.spring.extensions.getEvent

@RestController
@RequestMapping("/users")
class UserController(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
) {
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): User = getUserUseCase.execute(User.Id(id))

    @PutMapping("/me")
    fun updateUser(
        @AuthenticationPrincipal principal: OidcUser?,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<Any> {
        val presenter = UpdateUserPresenter()
        updateUserUseCase.execute(request.toRequest(principal), presenter)

        return presenter.getViewModel()
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.noContent().build()
    }
}

data class UpdateUserRequest(
    val email: String,
    val nickname: String
) {
    fun toRequest(principal: OidcUser?): UpdateUserUseCase.Request {
        when {
            nickname.toByteArray().size < 4 || nickname.toByteArray().size > 16 ->
                throw PlatformException("Nickname length must be between 4 and 16 Bytes.")
            else -> return UpdateUserUseCase.Request(principal?.userInfo?.email ?: email, nickname)
        }
    }
}

data class UpdateUserViewModel(
    val id: User.Id,
    val email: String,
    val nickname: String
)

class UpdateUserPresenter : Presenter {
    private var viewModel: UpdateUserViewModel? = null

    override fun present(vararg events: DomainEvent) {
        viewModel = events.getEvent(UserUpdatedEvent::class)?.toViewModel()
    }

    private fun UserUpdatedEvent.toViewModel(): UpdateUserViewModel =
        UpdateUserViewModel(id, email, nickname)

    fun getViewModel(): UpdateUserViewModel? = viewModel
}
