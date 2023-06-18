package tw.waterballsa.gaas.spring.it.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest

class OAuth2ControllerTest @Autowired constructor(
    val userRepository: UserRepository,
) : AbstractSpringBootTest() {

    val email = "user@example.com"
    val googleIdentityProviderId = "google-oauth2|102527320242660434908"
    val discordIdentityProviderId = "discord|102527320242660434908"

    @BeforeEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    fun givenInvalidJwtSubject_whenUserLogin_thenShouldLoginFailed() {
        givenInvalidJwt()
            .whenUserLogin()
            .thenShouldLoginFailed()
    }

    @Test
    fun givenOldEmail_andOldIdentityProviderId_whenUserLogin_thenLoginSuccessfully() {
        givenGoogleOAuth2Jwt()
            .whenUserLogin()
            .thenLoginSuccessfully()
    }

    @Test
    fun givenOldEmail_andNewIdentityProviderId_whenUserLogin_thenSaveNewIdentityProviderId() {
        givenDiscordOAuth2Jwt()
            .whenUserLogin()
            .thenSaveNewIdentityProviderId()
    }

    @Test
    fun givenNewEmail_andNewIdentityProviderId_whenUserLogin_thenCreateNewUser() {
        givenJwt(googleIdentityProviderId, email)
            .whenUserLogin()
            .thenCreateNewUser()
    }

    private fun givenInvalidJwt(): Jwt =
        Jwt("invalid_token",
            null,
            null,
            mapOf("alg" to "none"),
            mapOf("no_email" to "none"))

    private fun givenGoogleOAuth2Jwt(): Jwt {
        userRepository.createUser(mockUser)
        return givenJwt(googleIdentityProviderId, mockUser.email)
    }

    private fun givenDiscordOAuth2Jwt(): Jwt {
        userRepository.createUser(mockUser)
        return givenJwt(discordIdentityProviderId, mockUser.email)
    }

    private fun givenJwt(identityProviderId: String, email: String): Jwt =
        mockJwt(identityProviderId, email)

    private fun Jwt.whenUserLogin(): ResultActions =
        mockMvc.perform(get("/").with(jwt().jwt(this)))

    private fun ResultActions.thenShouldLoginFailed() {
        this.andExpect(status().isBadRequest)
    }

    private fun ResultActions.thenLoginSuccessfully() {
        this.andExpect(status().isOk)
    }

    private fun ResultActions.thenSaveNewIdentityProviderId() {
        thenLoginSuccessfully()
        userRepository.findByEmail(email)!!
            .thenSaveIdentityProviderId(googleIdentityProviderId)
            .thenSaveIdentityProviderId(discordIdentityProviderId)
    }

    private fun ResultActions.thenCreateNewUser() {
        thenLoginSuccessfully()
        userRepository.findByEmail(email)!!
            .thenSaveIdentityProviderId(googleIdentityProviderId)
    }

    private fun User.thenSaveIdentityProviderId(identityProviderId: String): User {
        assertThat(this).isNotNull
        assertThat(identities).isNotEmpty
        assertThat(identities).contains(identityProviderId)
        return this
    }

}
