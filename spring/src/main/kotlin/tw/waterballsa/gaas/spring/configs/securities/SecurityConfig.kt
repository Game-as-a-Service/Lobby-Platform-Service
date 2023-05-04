package tw.waterballsa.gaas.spring.configs.securities

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.SecurityFilterChain

@SecurityScheme(
    name = "Authorization",
    type = SecuritySchemeType.APIKEY,
    `in` = SecuritySchemeIn.HEADER
)
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests()
            .antMatchers("/walking-skeleton",
                "/v3/api-docs",
                "/v3/api-docs/swagger-config",
                "/swagger-ui/**"
            ).permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2Login().userInfoEndpoint().oidcUserService(oidcUserService())

        return http.build()
    }

    private fun oidcUserService(): OAuth2UserService<OidcUserRequest, OidcUser> {
        val userService = OidcUserService()
        return OAuth2UserService { request: OidcUserRequest? ->
            val oidcUser = userService.loadUser(request)
            DefaultOidcUser(
                emptyList(),
                oidcUser.idToken,
            )
        }
    }
}
