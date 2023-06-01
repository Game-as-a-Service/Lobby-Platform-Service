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
        private const val BEARER_TOKEN_HEADER = "Authorization"
        private const val BEARER_TOKEN_PREFIX = "Bearer "
    }

    private val registration = clientRegistrationRepository.findByRegistrationId(REGISTRATION_ID)
    private val jwtDecoder = OidcIdTokenDecoderFactory().createDecoder(registration)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain) {
        val idToken = request.getHeader(BEARER_TOKEN_HEADER)
            ?.takeIf { it.startsWith(BEARER_TOKEN_PREFIX) }
            ?.let { it.split(" ")[1] }
        if (idToken != null) {
            toOidcUser(idToken)?.also {
                SecurityContextHolder.getContext().authentication = OAuth2AuthenticationToken(
                    it,
                    emptyList<GrantedAuthority>(),
                    registration.registrationId
                )
            }
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
