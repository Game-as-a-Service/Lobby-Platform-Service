package tw.waterballsa.gaas.spring.configs.securities

import org.springframework.security.crypto.keygen.Base64StringKeyGenerator
import org.springframework.security.crypto.keygen.StringKeyGenerator
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames
import org.springframework.security.web.savedrequest.DefaultSavedRequest
import org.springframework.security.web.util.UrlUtils.buildFullRequestUrl
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.Base64.getUrlEncoder
import java.util.function.Consumer
import javax.servlet.http.HttpServletRequest

class CustomOAuthorizationRequestResolver(
    private val clientRegistrationRepository: ClientRegistrationRepository,
    authorizationRequestBaseUri: String
) : OAuth2AuthorizationRequestResolver {

    companion object {
        private const val REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId"
        private const val PATH_DELIMITER = '/'
        private val DEFAULT_STATE_GENERATOR: StringKeyGenerator = Base64StringKeyGenerator(getUrlEncoder())
        private val DEFAULT_SECURE_KEY_GENERATOR: StringKeyGenerator =
            Base64StringKeyGenerator(getUrlEncoder().withoutPadding(), 96)
        private val DEFAULT_PKCE_APPLIER = OAuth2AuthorizationRequestCustomizers.withPkce()

        private fun expandRedirectUri(
            request: HttpServletRequest, clientRegistration: ClientRegistration,
            action: String = ""
        ): String {
            val uriVariables: MutableMap<String, String?> = HashMap()
            uriVariables["registrationId"] = clientRegistration.registrationId
            // @formatter:off
            val uriComponents = UriComponentsBuilder.fromHttpUrl(buildFullRequestUrl(request))
                .replacePath(request.contextPath)
                .replaceQuery(null)
                .build()
            // @formatter:on
            uriVariables["baseScheme"] = uriComponents.scheme ?: ""
            uriVariables["baseHost"] = uriComponents.host ?: ""
            // following logic is based on HierarchicalUriComponents#toUriString()
            val port = uriComponents.port
            uriVariables["basePort"] = if (port == -1) "" else ":$port"
            uriVariables["basePath"] = parsePath(uriComponents)
            uriVariables["baseUrl"] = uriComponents.toUriString()
            uriVariables["action"] = action

            return UriComponentsBuilder
                .fromUriString(clientRegistration.redirectUri)
                .buildAndExpand(uriVariables)
                .toUriString()
        }

        private fun parsePath(uriComponents: UriComponents): String {
            var path = uriComponents.path
            if (StringUtils.hasLength(path)) {
                if (path!![0] != PATH_DELIMITER) {
                    path = "$PATH_DELIMITER$path"
                }
            }
            return path ?: ""
        }

        private fun applyNonce(builder: OAuth2AuthorizationRequest.Builder) {
            try {
                val nonce = DEFAULT_SECURE_KEY_GENERATOR.generateKey()
                val nonceHash = createHash(nonce)
                builder.attributes { attrs: MutableMap<String?, Any?> ->
                    attrs[OidcParameterNames.NONCE] = nonce
                }
                builder.additionalParameters { params: MutableMap<String?, Any?> ->
                    params[OidcParameterNames.NONCE] = nonceHash
                }
            } catch (_: NoSuchAlgorithmException) {
            }
        }

        @Throws(NoSuchAlgorithmException::class)
        private fun createHash(value: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(value.toByteArray(StandardCharsets.US_ASCII))
            return getUrlEncoder().withoutPadding().encodeToString(digest)
        }

    }

    private val authorizationRequestMatcher: AntPathRequestMatcher = AntPathRequestMatcher(
        "$authorizationRequestBaseUri/{$REGISTRATION_ID_URI_VARIABLE_NAME}"
    )

    private var authorizationRequestCustomizer =
        Consumer { _: OAuth2AuthorizationRequest.Builder -> }

    override fun resolve(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        val registrationId = resolveRegistrationId(request) ?: return null
        val redirectUriAction = request.getAction("login")
        val originalRequest = request.session.getAttribute("SPRING_SECURITY_SAVED_REQUEST") as DefaultSavedRequest
        val identityProviders = IdentityProvider.values().map { it.queryParam }
        val targetIdentityProvider = originalRequest.parameterMap["type"]?.find { it in identityProviders }
        authorizationRequestCustomizer = Consumer {
            it.parameters { params -> params["connection"] = targetIdentityProvider ?: "google-oauth2" }
        }

        return resolve(request, registrationId, redirectUriAction)!!
    }

    override fun resolve(request: HttpServletRequest, registrationId: String?): OAuth2AuthorizationRequest? {
        val redirectUriAction = request.getAction("authorize")
        return resolve(request, registrationId ?: "auth0", redirectUriAction)
    }


    private fun HttpServletRequest.getAction(defaultAction: String): String = getParameter("action") ?: defaultAction

    private fun resolve(
        request: HttpServletRequest,
        registrationId: String?,
        redirectUriAction: String
    ): OAuth2AuthorizationRequest? {
        if (registrationId == null) {
            return null
        }
        val clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId)
            ?: throw IllegalArgumentException("Invalid Client Registration with Id: $registrationId")
        val builder = getBuilder(clientRegistration)
        val redirectUriStr = expandRedirectUri(request, clientRegistration, redirectUriAction)

        // @formatter:off
        builder.clientId(clientRegistration.clientId)
            .authorizationUri(clientRegistration.providerDetails.authorizationUri)
            .redirectUri(redirectUriStr)
            .scopes(clientRegistration.scopes)
            .state(DEFAULT_STATE_GENERATOR.generateKey())
        // @formatter:on
        authorizationRequestCustomizer.accept(builder)
        return builder.build()
    }

    private fun getBuilder(clientRegistration: ClientRegistration): OAuth2AuthorizationRequest.Builder {
        if (AuthorizationGrantType.AUTHORIZATION_CODE == clientRegistration.authorizationGrantType) {
            // @formatter:off
            val builder = OAuth2AuthorizationRequest.authorizationCode()
                .attributes{attrs:MutableMap<String?, Any?> -> attrs[OAuth2ParameterNames.REGISTRATION_ID] = clientRegistration.registrationId}
            // @formatter:on
            if (!CollectionUtils.isEmpty(clientRegistration.scopes)
                && clientRegistration.scopes.contains(OidcScopes.OPENID)
            ) {
                // Section 3.1.2.1 Authentication Request -
                // https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest scope
                // REQUIRED. OpenID Connect requests MUST contain the "openid" scope
                // value.
                applyNonce(builder)
            }
            if (ClientAuthenticationMethod.NONE == clientRegistration.clientAuthenticationMethod) {
                DEFAULT_PKCE_APPLIER.accept(builder)
            }
            return builder
        }
        if (AuthorizationGrantType.IMPLICIT == clientRegistration.authorizationGrantType) {
            return OAuth2AuthorizationRequest.implicit()
        }
        throw IllegalArgumentException(
            "Invalid Authorization Grant Type (${clientRegistration.authorizationGrantType.value}) for Client Registration with Id: ${clientRegistration.registrationId}"
        )
    }

    private fun resolveRegistrationId(request: HttpServletRequest): String? {
        return if (authorizationRequestMatcher.matches(request)) {
            authorizationRequestMatcher.matcher(request).variables[REGISTRATION_ID_URI_VARIABLE_NAME]
        } else null
    }
}

