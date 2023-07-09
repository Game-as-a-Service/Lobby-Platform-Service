package tw.waterballsa.gaas.spring.controllers

import org.springframework.http.HttpHeaders.*
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tw.waterballsa.gaas.application.usecases.CreateUserUseCase
import tw.waterballsa.gaas.exceptions.PlatformException

@RestController
class OAuth2Controller(
    private val createUserUseCase: CreateUserUseCase,
) {

    @GetMapping
    fun home(@AuthenticationPrincipal principal: Jwt): String {
        createUserUseCase.execute(principal.toRequest())
        return principal.tokenValue ?: "index"
    }

    @GetMapping("/login")
    fun login(@RequestParam type: String): ResponseEntity<Unit>{
        return ResponseEntity.status(FOUND)
            .header(LOCATION, "/oauth2/authorization/auth0?type=$type")
            .build()
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
