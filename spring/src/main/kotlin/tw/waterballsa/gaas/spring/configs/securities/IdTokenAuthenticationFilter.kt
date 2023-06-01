package tw.waterballsa.gaas.spring.configs.securities

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
): OncePerRequestFilter() {
    companion object{
        private const val REGISTRATION_ID = "auth0"
    }

    private val registration = clientRegistrationRepository.findByRegistrationId(REGISTRATION_ID)
    private val jwtDecoder = OidcIdTokenDecoderFactory().createDecoder(registration)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain) {
        request.bearerToken()
            ?.let { toOidcUser(it) }
            ?.run {
                SecurityContextHolder.getContext().authentication = OAuth2AuthenticationToken(
                    this,
                    emptyList<GrantedAuthority>(),
                    registration.registrationId
                )
            }
        filterChain.doFilter(request, response)
    }

    private fun toOidcUser(idTokenValue: String): OidcUser?{
        var oidcUser: OidcUser? = null
        try{
            oidcUser = jwtDecoder.decode(idTokenValue)
                ?.let { OidcIdToken(it.tokenValue, it.issuedAt, it.expiresAt, it.claims) }
                ?.let { DefaultOidcUser(emptyList(), it)}
        }catch (e: JwtException){
            // id token not accept
        }
        return oidcUser
    }
}
private fun HttpServletRequest.bearerToken(): String? = this.getHeader("Authorization")
    ?.takeIf { it.startsWith("Bearer ") }
    ?.let { it.split(" ")[1] }