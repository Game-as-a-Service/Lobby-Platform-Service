package tw.waterballsa.gaas.spring.configs.securities

import com.nimbusds.jwt.JWTParser
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidationException
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.context.request.NativeWebRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class RefreshAccessTokenHandler(
    private val jwtDecoder: JwtDecoder,
    private val authorizedClientService: OAuth2AuthorizedClientService,
    private val manger: OAuth2AuthorizedClientManager,
) {
    companion object{
        private const val CLIENT_REGISTRATION_ID = "auth0"
    }

    fun refreshAccessToken(
        accessTokenValue: String,
        request: NativeWebRequest
    ): String?{
        val principalName = accessTokenValue.toPrincipalName()
            ?.takeIf { it.canRefreshToken() }
            ?: return null

        val authorizedClient =
            manger.authorize(request.toOAuth2AuthorizeRequest(principalName)) ?: return null

        if(authorizedClient.accessToken.tokenValue != accessTokenValue){
            saveAuthorizedClient(authorizedClient)
        }

        return authorizedClient.accessToken.tokenValue
    }

    private fun saveAuthorizedClient(authorizedClient: OAuth2AuthorizedClient){
        val authentication = JwtAuthenticationToken(jwtDecoder.decode(authorizedClient.accessToken.tokenValue))
        authorizedClientService.saveAuthorizedClient(authorizedClient, authentication)
    }

    private fun JwtValidationException.onlyExpiredAt() =
        errors.takeIf { it.size == 1 }
            ?.first()
            ?.takeIf { it.description.startsWith("Jwt expired at") }
            ?.let { true }
            ?: false

    private fun String.toPrincipalName() = try {
        jwtDecoder.decode(this).subject
    } catch (e: JwtValidationException) {
        e.takeIf { it.onlyExpiredAt() }
            .let { JWTParser.parse(this).jwtClaimsSet.subject }
    } catch (e: Exception) {
        null
    }

    private fun String.canRefreshToken() =
        authorizedClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(CLIENT_REGISTRATION_ID, this)
            ?.takeIf { it.refreshToken != null }
            ?.let { true }
            ?: false

    private fun NativeWebRequest.toOAuth2AuthorizeRequest(principalName: String) = OAuth2AuthorizeRequest
        .withClientRegistrationId(CLIENT_REGISTRATION_ID)
        .principal(
            UsernamePasswordAuthenticationToken(
                User(principalName, "", emptyList()), null, emptyList()
            )
        )
        .attribute(HttpServletRequest::class.java.name, this.nativeRequest)
        .attribute(HttpServletResponse::class.java.name, this.nativeResponse)
        .build()
}