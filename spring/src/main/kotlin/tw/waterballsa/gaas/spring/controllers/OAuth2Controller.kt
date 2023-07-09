package tw.waterballsa.gaas.spring.controllers

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.*
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.util.UriComponentsBuilder
import tw.waterballsa.gaas.application.usecases.CreateUserUseCase
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.spring.utils.WebClientUtils

@RestController
class OAuth2Controller(
    private val createUserUseCase: CreateUserUseCase,
) {

    @Value("\${frontend}")
    private lateinit var frontendUrl: String

    @GetMapping
    fun home(@AuthenticationPrincipal principal: Jwt): String {
        createUserUseCase.execute(principal.toRequest())
        return principal.tokenValue ?: "index"
    }

    @GetMapping("/login-successfully")
    fun loginSuccessfully(@AuthenticationPrincipal principal: OidcUser): ResponseEntity<Any> {
        sendTokenToFrontend(principal.idToken)
        return ResponseEntity.status(FOUND).header(LOCATION, "/").build()
    }

    @GetMapping("/login")
    fun login(@RequestParam type: String, redirectAttributes: RedirectAttributes): RedirectView{
        redirectAttributes["type"] = type
        return RedirectView("oauth2/authorization/auth0")
    }

    private fun sendTokenToFrontend(oidcIdToken: OidcIdToken) {
        val uriString = UriComponentsBuilder.fromUriString(frontendUrl)
            .queryParam("token", oidcIdToken.tokenValue)
            .toUriString()

        WebClientUtils.get(uriString, String::class.java)
    }
}

val Jwt.email: String
    get() = claims["email"]?.toString()
        ?: throw PlatformException("JWT email should exist.")

val Jwt.identityProviderId: String
    get() = subject
        ?: throw PlatformException("JWT subject should exist.")

private fun Jwt.toRequest(): CreateUserUseCase.Request =
    CreateUserUseCase.Request(email, identityProviderId)
