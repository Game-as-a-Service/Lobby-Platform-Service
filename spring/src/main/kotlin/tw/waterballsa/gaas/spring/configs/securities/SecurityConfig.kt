package tw.waterballsa.gaas.spring.configs.securities

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED

@EnableWebSecurity
class SecurityConfig(
    private val clientRegistrationRepository: ClientRegistrationRepository,
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .antMatchers("/health", "/walking-skeleton").permitAll()
            .antMatchers("/swagger-ui/**", "/favicon.ico").permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2Login()
            .defaultSuccessUrl("/login-successfully", true)
            .authorizationEndpoint()
            .authorizationRequestResolver(
                CustomOAuthorizationRequestResolver(
                    clientRegistrationRepository,
                    "/oauth2/authorization"
                )
            )
            .and()
            .userInfoEndpoint().oidcUserService(oidcUserService())
            .and()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(redirectToLoginEndPoint())

        return http.build()
    }

    private fun oidcUserService(): OAuth2UserService<OidcUserRequest, OidcUser> {
        val userService = OidcUserService()
        return OAuth2UserService { request: OidcUserRequest? ->
            val oidcUser = userService.loadUser(request)
            val oidcIdToken = oidcUser.idToken
            DefaultOidcUser(emptyList(), oidcIdToken)
        }
    }

    private fun redirectToLoginEndPoint(): AuthenticationEntryPoint =
        AuthenticationEntryPoint { request, response, _ ->
            when (request.requestURI) {
                "/login" -> response.sendRedirect("/oauth2/authorization/auth0")
                else -> response.sendError(SC_UNAUTHORIZED)
            }
        }
}
