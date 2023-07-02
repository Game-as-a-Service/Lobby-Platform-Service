package tw.waterballsa.gaas.spring.controllers

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import tw.waterballsa.gaas.application.usecases.GetUserUseCase
import tw.waterballsa.gaas.application.usecases.UpdateUserUseCase
import tw.waterballsa.gaas.spring.controllers.presenter.GetUserPresenter
import tw.waterballsa.gaas.spring.controllers.presenter.UpdateUserPresenter
import tw.waterballsa.gaas.spring.controllers.viewmodel.GetUserViewModel
import tw.waterballsa.gaas.spring.controllers.viewmodel.UpdateUserViewModel

@RestController
@RequestMapping("/users")
class UserController(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
) {
    @GetMapping("/me")
    fun getUser(@AuthenticationPrincipal principal: Jwt): GetUserViewModel {
        val request = principal.toRequest()
        val presenter = GetUserPresenter()
        getUserUseCase.execute(request, presenter)
        return presenter.viewModel
    }

    @PutMapping("/me")
    fun updateUser(
        @AuthenticationPrincipal principal: Jwt,
        @RequestBody updateUserRequest: UpdateUserRequest,
    ): UpdateUserViewModel {
        val request = updateUserRequest.toRequest(principal)
        val presenter = UpdateUserPresenter()
        updateUserUseCase.execute(request, presenter)
        return presenter.getViewModel()
    }
}

private fun Jwt.toRequest(): GetUserUseCase.Request =
    GetUserUseCase.Request(claims["email"] as String)

data class UpdateUserRequest(val nickname: String) {
    fun toRequest(principal: Jwt): UpdateUserUseCase.Request {
        val email = principal.claims["email"] as String
        return UpdateUserUseCase.Request(email, nickname)
    }
}

