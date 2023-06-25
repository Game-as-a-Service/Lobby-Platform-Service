package tw.waterballsa.gaas.spring.controllers

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.waterballsa.gaas.application.usecases.GetUserUseCase
import tw.waterballsa.gaas.spring.controllers.presenter.GetUserPresenter
import tw.waterballsa.gaas.spring.controllers.viewmodel.GetUserViewModel

@RestController
@RequestMapping("/users")
class UserController(
    private val getUserUseCase: GetUserUseCase
) {
    @GetMapping("/me")
    fun getUser(@AuthenticationPrincipal principal: Jwt): GetUserViewModel {
        val request = principal.toRequest()
        val presenter = GetUserPresenter()
        getUserUseCase.execute(request, presenter)
        return presenter.viewModel
    }
}

private fun Jwt.toRequest(): GetUserUseCase.Request =
    GetUserUseCase.Request(claims["email"] as String)
