package tw.waterballsa.gaas.spring.configs.securities

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import tw.waterballsa.gaas.application.usecases.CreateUserUseCase

@EnableWebSecurity
class SecurityConfig(
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val authorizedClientService: OAuth2AuthorizedClientService,
    private val createUserUseCase: CreateUserUseCase
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .antMatchers("/login", "/health", "/walking-skeleton").permitAll()
            .antMatchers("/swagger-ui/**", "/favicon.ico").permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2Login()
            .successHandler(successHandler())
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
            .oauth2ResourceServer().jwt()

        return http.build()
    }

    @Bean
    fun successHandler(): AuthenticationSuccessHandler{
        return CustomSuccessHandler(authorizedClientService, createUserUseCase);
    }

    private fun oidcUserService(): OAuth2UserService<OidcUserRequest, OidcUser> {
        val userService = OidcUserService()
        return OAuth2UserService { request: OidcUserRequest? ->
            val oidcUser = userService.loadUser(request)
            val oidcIdToken = oidcUser.idToken
            DefaultOidcUser(emptyList(), oidcIdToken)
        }
    }

}
