package tw.waterballsa.gaas.spring.it.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest

class OAuth2ControllerTest @Autowired constructor(
    val userRepository: UserRepository,
) : AbstractSpringBootTest() {

    private final val googleIdentityProviderId = "google-oauth2|102527320242660434908"
    private final val discordIdentityProviderId = "discord|102527320242660434908"

    private final val googleOAuth2OidcUser = googleIdentityProviderId.toOidcUser()
    private final val discordOAuth2OidcUser = discordIdentityProviderId.toOidcUser()

    private final val invalidOidcUser = DefaultOidcUser(
        emptyList(),
        OidcIdToken.withTokenValue("oidc-token-value")
            .subject("invalid-user")
            .claim("no_email", "none")
            .build(),
    )

    @BeforeEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    fun whenUserLoginWithInvalidJwt_thenShouldLoginFailed() {
        whenUserLogin(invalidOidcUser)
            .thenShouldLoginFailed()
    }

    @Test
    fun givenUserHasLoggedInViaGoogle_whenUserLoginWithGoogleOAuth2Jwt_thenLoginSuccessfully() {
        givenUserHasLoggedInViaGoogle()
        whenUserLogin(googleOAuth2OidcUser)
            .thenLoginSuccessfully()
    }

    @Test
    fun givenUserHasLoggedInViaGoogle_whenUserLoginWithDiscordOAuth2Jwt_thenUserHaveNewIdentity() {
        givenUserHasLoggedInViaGoogle()
        whenUserLogin(discordOAuth2OidcUser)
            .thenUserHaveNewIdentity(googleIdentityProviderId, discordIdentityProviderId)
    }

    @Test
    fun whenUserLoginAtTheFirstTime_thenCreateNewUser() {
        whenUserLogin(googleOAuth2OidcUser)
            .thenCreateNewUser()
    }

    private fun givenUserHasLoggedInViaGoogle(): User =
        userRepository.createUser(mockUser)

    private fun whenUserLogin(oidcUser: OidcUser): ResultActions =
        mockMvc.perform(get("/").with(oidcLogin().oidcUser(oidcUser)))

    private fun ResultActions.thenShouldLoginFailed() {
        andExpect(status().isBadRequest)
    }

    private fun ResultActions.thenLoginSuccessfully() {
        andExpect(status().isOk)
    }

    private fun ResultActions.thenUserHaveNewIdentity(vararg identityProviderIds: String) {
        thenLoginSuccessfully()
        userRepository.findByEmail(mockUser.email)
            ?.thenWouldHaveIdentityProviderIds(*identityProviderIds)
    }

    private fun ResultActions.thenCreateNewUser() {
        thenLoginSuccessfully()
        userRepository.findByEmail(mockUser.email)
            .thenNickNameShouldBeRandomName()
            .thenWouldHaveIdentityProviderIds(googleIdentityProviderId)
    }

    private fun User?.thenWouldHaveIdentityProviderIds(vararg identityProviderIds: String): User {
        assertThat(this).isNotNull
        assertThat(this!!.identities).containsAll(identityProviderIds.toList())
        return this
    }

    private fun User?.thenNickNameShouldBeRandomName(): User {
        assertThat(this).isNotNull
        assertThat(this!!.nickname).startsWith("user_")
        return this
    }

    private fun String.toOidcUser(): OidcUser =
        DefaultOidcUser(
            emptyList(),
            OidcIdToken.withTokenValue("oidc-token-value")
                .subject(this)
                .claim("email", mockUser.email)
                .build(),
        )
}
