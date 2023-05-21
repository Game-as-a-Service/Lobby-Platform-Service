package tw.waterballsa.gaas.spring.it.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = ["dev"])
class OAuth2ControllerTest @Autowired constructor(
    val userRepository: UserRepository,
) : AbstractSpringBootTest() {

    @BeforeEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    fun givenInvalidUserInfo_whenUserLogin_thenLoginFailed() {
        givenInvalidUserInfo().thenLoginFailed()
    }

    @Test
    fun givenNewUser_whenUserLogin_thenCreateUser() {
        givenNewUserInfo().assertUserExists(false)
            .loginSuccessfully().assertUserExists(true)
    }

    @Test
    fun givenOldUser_whenUserLogin_thenLoginSuccessfully() {
        givenUserLoginBefore().assertUserExists(true)
            .loginSuccessfully().assertUserExists(true)
    }

    private fun givenInvalidUserInfo(): OidcUser = givenUserInfo(null)
    private fun givenNewUserInfo(): OidcUser = givenUserInfo(OidcUserInfo(mapOf("email" to "user@example.com")))
    private fun givenOldUserInfo(): OidcUser = givenUserInfo(OidcUserInfo(mapOf("email" to "other@example.com")))

    private fun givenUserInfo(oidcUserInfo: OidcUserInfo?): OidcUser = DefaultOidcUser(
        listOf(),
        OidcIdToken("token", null, null, mapOf("sub" to "my_sub")),
        oidcUserInfo
    )

    private fun givenUserLoginBefore(): OidcUser = givenOldUserInfo().loginSuccessfully()

    private fun OidcUser.assertUserExists(isExists: Boolean): OidcUser = this.also {
        assertThat(userRepository.existsUserByEmail(userInfo.email)).isEqualTo(isExists)
    }

    private fun OidcUser.login(): ResultActions =
        mockMvc.perform(get("/").with(oidcLogin().oidcUser(this)))

    private fun OidcUser.loginSuccessfully(): OidcUser = this.also {
        login().andExpect(status().isOk)
    }

    private fun OidcUser.thenLoginFailed(): ResultActions = login().andExpect(status().isBadRequest)

}
