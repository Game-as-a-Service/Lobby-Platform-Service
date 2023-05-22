package tw.waterballsa.gaas.spring.it.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
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

    @BeforeEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    fun givenInvalidUserInfo_whenUserLogin_thenShouldLoginFailed() {
        givenInvalidUserInfo()
            .whenLogin()
            .thenShouldLoginFailed()
    }

    @Test
    fun givenNewUser_whenUserLogin_thenCreateUser() {
        givenNewUserInfo().assertUserNotExists()
            .whenLogin()
            .thenShouldLoginSuccessfully()
    }

    @Test
    fun givenOldUser_whenUserLogin_thenShouldLoginSuccessfully() {
        givenOldUserInfo().assertUserExists()
            .whenLogin()
            .thenShouldLoginSuccessfully()
    }

    private fun givenInvalidUserInfo(): OidcUser = givenUserInfo(null)

    private fun givenNewUserInfo(): OidcUser = givenUserInfo(OidcUserInfo(mapOf("email" to "user@example.com")))

    private fun givenOldUserInfo(): OidcUser {
        val userInfo = givenUserInfo(OidcUserInfo(mapOf("email" to "other@example.com")))
        userRepository.createUser(User(email = userInfo.email))
        return userInfo
    }

    private fun givenUserInfo(oidcUserInfo: OidcUserInfo?): OidcUser = DefaultOidcUser(
        listOf(),
        OidcIdToken("token", null, null, mapOf("sub" to "my_sub")),
        oidcUserInfo
    )

    private fun OidcUser.assertUserExists(): OidcUser = this.also {
        assertThat(userRepository.existsUserByEmail(userInfo.email)).isTrue()
    }

    private fun OidcUser.assertUserNotExists(): OidcUser = this.also {
        assertThat(userRepository.existsUserByEmail(userInfo.email)).isFalse()
    }

    private fun OidcUser.whenLogin(): ResultActions =
        mockMvc.perform(get("/").with(oidcLogin().oidcUser(this)))

    private fun ResultActions.thenShouldLoginSuccessfully(): ResultActions =
        andExpect(status().isOk)

    private fun ResultActions.thenShouldLoginFailed(): ResultActions =
        andExpect(status().isBadRequest)

}
