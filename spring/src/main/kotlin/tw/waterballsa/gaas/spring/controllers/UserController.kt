package tw.waterballsa.gaas.spring.controllers

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.waterballsa.gaas.application.usecases.GetUserMeUseCase
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.controllers.presenter.GetUserMePresenter
import tw.waterballsa.gaas.spring.controllers.viewmodel.GetUserMeViewModel

@RestController
@RequestMapping("/users")
class UserController(
    private val getUserMeUseCase: GetUserMeUseCase
) {
    @GetMapping("/me")
    fun getUserMe(@AuthenticationPrincipal principal: Jwt): GetUserMeViewModel {
        val request = principal.toRequest()
        val presenter = GetUserMePresenter()
        getUserMeUseCase.execute(request, presenter)
        return presenter.viewModel
    }
}

private fun Jwt.toRequest(): GetUserMeUseCase.Request =
    GetUserMeUseCase.Request(claims["email"] as String)
