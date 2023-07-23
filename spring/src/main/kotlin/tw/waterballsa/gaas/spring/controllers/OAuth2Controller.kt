package tw.waterballsa.gaas.spring.controllers

import org.springframework.http.HttpHeaders.*
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.*
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.NativeWebRequest
import tw.waterballsa.gaas.application.usecases.CreateUserUseCase
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.spring.configs.securities.RefreshAccessTokenHandler

@RestController
class OAuth2Controller(
    private val createUserUseCase: CreateUserUseCase,
    private val refreshTokenHandler: RefreshAccessTokenHandler,
) {

    @GetMapping
    fun home(
        @AuthenticationPrincipal principal: OidcUser,
        @RegisteredOAuth2AuthorizedClient client: OAuth2AuthorizedClient
    ): String {
        createUserUseCase.execute(principal.toRequest())
        return client.accessToken.tokenValue ?: "index"
    }

    @PostMapping("/authenticate")
    fun authenticate(
        request: NativeWebRequest,
        @RequestBody payload: AuthenticateToken
    ): AuthenticateToken{
        return refreshTokenHandler.refreshAccessToken(request, payload.token)
            ?.let { AuthenticateToken(it) }
            ?: throw notFound("AccessToken").message()
    }

    @GetMapping("/login")
    fun login(@RequestParam type: String): ResponseEntity<Unit>{
        return ResponseEntity.status(FOUND)
            .header(LOCATION, "/oauth2/authorization/auth0?type=$type")
            .build()
    }
}

data class AuthenticateToken(
    val token: String
)

val OidcUser.identityProviderId: String
    get() = subject
        ?: throw PlatformException("subject should exist.")

private fun OidcUser.toRequest(): CreateUserUseCase.Request =
    CreateUserUseCase.Request(email ?: throw PlatformException("email should exist."), identityProviderId)
