package tw.waterballsa.gaas.spring.configs.securities

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class IdTokenAuthenticationFilter(
    clientRegistrationRepository: ClientRegistrationRepository
) : OncePerRequestFilter() {
    companion object {
        private const val REGISTRATION_ID = "auth0"
    }

    private val registration by lazy { clientRegistrationRepository.findByRegistrationId(REGISTRATION_ID) }
    private val jwtDecoder by lazy { OidcIdTokenDecoderFactory().createDecoder(registration) }

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        request.bearerToken()
            ?.let { toOidcUser(it) }
            ?.run {
                SecurityContextHolder.getContext().authentication = OAuth2AuthenticationToken(
                    this, emptyList<GrantedAuthority>(), registration.registrationId
                )
            }
        filterChain.doFilter(request, response)
    }

    private fun toOidcUser(idTokenValue: String): OidcUser? = try {
        jwtDecoder.decode(idTokenValue)
            ?.let { OidcIdToken(it.tokenValue, it.issuedAt, it.expiresAt, it.claims) }
            ?.let { DefaultOidcUser(emptyList(), it) }
    } catch (e: JwtException) {
        // id token not accept
        null
    }
}

private fun HttpServletRequest.bearerToken(): String? = this.getHeader(AUTHORIZATION)
    ?.takeIf { it.startsWith("Bearer ") }
    ?.split(" ")
    ?.last()