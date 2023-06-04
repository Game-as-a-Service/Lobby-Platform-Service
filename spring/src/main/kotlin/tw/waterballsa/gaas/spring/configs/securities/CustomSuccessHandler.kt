package tw.waterballsa.gaas.spring.configs.securities

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import tw.waterballsa.gaas.application.usecases.CreateUserUseCase
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomSuccessHandler(
    private val authorizedClientService: OAuth2AuthorizedClientService,
    private val createUserUseCase: CreateUserUseCase
) : AuthenticationSuccessHandler {
    @Value("\${frontend}")
    private lateinit var frontendUrl: String

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        authentication as OAuth2AuthenticationToken

        val email = authentication.principal.let { it as OidcUser }.email
        createUserUseCase.execute(CreateUserUseCase.Request(email))

        val accessTokenValue = authorizedClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(
            authentication.authorizedClientRegistrationId,
            authentication.name
        )
            .accessToken.tokenValue
        response.sendRedirect("$frontendUrl/auth/token/$accessTokenValue")
    }
}