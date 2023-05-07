package tw.waterballsa.gaas.spring.controllers

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import tw.waterballsa.gaas.application.usecases.CreateUserUseCase
import java.util.*

@RestController
class OAuth2Controller(
    private val createUserUseCase: CreateUserUseCase
) {
    @GetMapping("/")
    fun home(@AuthenticationPrincipal principal: OidcUser?): String {
        createUserUseCase.execute(principal.toRequest())
        return principal?.idToken?.tokenValue ?: "index"
    }
}

fun OidcUser?.toRequest(): CreateUserUseCase.Request = CreateUserUseCase.Request(
    this?.userInfo?.email ?: throw IllegalArgumentException("User email is null")
)