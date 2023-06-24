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

    @BeforeEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    fun whenUserLoginWithInvalidJwt_thenShouldLoginFailed() {
        whenUserLoginWithInvalidJwt()
            .thenShouldLoginFailed()
    }

    @Test
    fun givenUserHasLoggedInViaGoogle_whenUserLoginWithGoogleOAuth2Jwt_thenLoginSuccessfully() {
        givenUserHasLoggedInViaGoogle()
        whenUserLogin(googleIdentityProviderId)
            .thenLoginSuccessfully()
    }

    @Test
    fun givenUserHasLoggedInViaGoogle_whenUserLoginWithDiscordOAuth2Jwt_thenUserHaveNewIdentity() {
        givenUserHasLoggedInViaGoogle()
        whenUserLogin(discordIdentityProviderId)
            .thenUserHaveNewIdentity()
    }

    @Test
    fun whenUserLoginWithNewIdentity_thenCreateNewUser() {
        whenUserLogin(googleIdentityProviderId)
            .thenCreateNewUser()
    }

    private fun givenUserHasLoggedInViaGoogle(): User =
        userRepository.createUser(mockUser)

    private fun whenUserLoginWithInvalidJwt(): ResultActions {
        val invalidJwt = Jwt(
            "invalid_token",
            null,
            null,
            mapOf("alg" to "none"),
            mapOf("no_email" to "none")
        )
        return mockMvc.perform(get("/").withJwt(invalidJwt))
    }

    private fun whenUserLogin(identityProviderId: String): ResultActions =
        mockMvc.perform(get("/").withIdentityProviderId(identityProviderId))

    private fun ResultActions.thenShouldLoginFailed() {
        this.andExpect(status().isBadRequest)
    }

    private fun ResultActions.thenLoginSuccessfully() {
        this.andExpect(status().isOk)
    }

    private fun ResultActions.thenUserHaveNewIdentity() {
        thenLoginSuccessfully()
        userRepository.findByEmail(mockUser.email)!!
            .thenSaveIdentityProviderId(googleIdentityProviderId)
            .thenSaveIdentityProviderId(discordIdentityProviderId)
    }

    private fun ResultActions.thenCreateNewUser() {
        thenLoginSuccessfully()
        userRepository.findByEmail(mockUser.email)!!
            .thenCreateNickname()
            .thenSaveIdentityProviderId(googleIdentityProviderId)
    }

    private fun User.thenSaveIdentityProviderId(identityProviderId: String): User {
        assertThat(this).isNotNull
        assertThat(identities).isNotEmpty
        assertThat(identities).contains(identityProviderId)
        return this
    }

    private fun User.thenCreateNickname(): User {
        assertThat(this).isNotNull
        assertThat(nickname).startsWith("user_")
        return this
    }

}
