package tw.waterballsa.gaas.spring.it.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
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

    private final val googleOAuth2Jwt = googleIdentityProviderId.toJwt()
    private final val discordOAuth2Jwt = discordIdentityProviderId.toJwt()

    val invalidJwt = Jwt(
        "invalid_token",
        null,
        null,
        mapOf("alg" to "none"),
        mapOf("no_email" to "none")
    )

    @BeforeEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    fun whenUserLoginWithInvalidJwt_thenShouldLoginFailed() {
        whenUserLogin(invalidJwt)
            .thenShouldLoginFailed()
    }

    @Test
    fun givenUserHasLoggedInViaGoogle_whenUserLoginWithGoogleOAuth2Jwt_thenLoginSuccessfully() {
        givenUserHasLoggedInViaGoogle()
        whenUserLogin(googleOAuth2Jwt)
            .thenLoginSuccessfully()
    }

    @Test
    fun givenUserHasLoggedInViaGoogle_whenUserLoginWithDiscordOAuth2Jwt_thenUserHaveNewIdentity() {
        givenUserHasLoggedInViaGoogle()
        whenUserLogin(discordOAuth2Jwt)
            .thenUserHaveNewIdentity()
    }

    @Test
    fun whenUserLoginAtTheFirstTime_thenCreateNewUser() {
        whenUserLogin(googleOAuth2Jwt)
            .thenCreateNewUser()
    }

    private fun givenUserHasLoggedInViaGoogle(): User =
        userRepository.createUser(mockUser)

    private fun whenUserLogin(jwt: Jwt): ResultActions =
        mockMvc.perform(get("/").withJwt(jwt))

    private fun ResultActions.thenShouldLoginFailed() {
        andExpect(status().isBadRequest)
    }

    private fun ResultActions.thenLoginSuccessfully() {
        andExpect(status().isOk)
    }

    private fun ResultActions.thenUserHaveNewIdentity() {
        thenLoginSuccessfully()
        userRepository.findByEmail(mockUser.email)
            ?.thenWouldHaveIdentityProviderIds(googleIdentityProviderId, discordIdentityProviderId)
    }

    private fun ResultActions.thenCreateNewUser() {
        thenLoginSuccessfully()
        userRepository.findByEmail(mockUser.email)
            ?.thenNickNameShouldBeRandomName()
            ?.thenWouldHaveIdentityProviderIds(googleIdentityProviderId)
    }

    private fun User.thenWouldHaveIdentityProviderIds(vararg identityProviderIds: String): User {
        assertThat(identities).containsAll(identityProviderIds.toList())
        return this
    }

    private fun User.thenNickNameShouldBeRandomName(): User {
        assertThat(nickname).startsWith("user_")
        return this
    }

}
