package tw.waterballsa.gaas.spring.controllers

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.waterballsa.gaas.application.usecases.GetUserUseCase
import tw.waterballsa.gaas.domain.User

@RestController
@RequestMapping("/users")
class UserController(
    private val getUserUseCase: GetUserUseCase
) {
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): User = getUserUseCase.execute(User.Id(id))

    @GetMapping("/me")
    fun getUserMe(@AuthenticationPrincipal principal: OidcUser): User =
        getUserUseCase.execute(User.Id(principal.subject))
}
