package tw.waterballsa.gaas.spring.it.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
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
    fun givenInvalidJwtSubject_whenUserLogin_thenShouldLoginFailed() {
        givenInvalidJwtSubject()
            ?.whenUserLogin()
            ?.thenShouldLoginFailed()
    }

    @Test
    fun givenNewUserWithJwtSubject_whenUserLogin_thenCreateUser() {
        givenNewUserWithJwtSubject()
            .whenUserLogin()
            .thenCreateUser()
    }

    @Test
    fun givenOldUser_whenUserLogin_thenShouldLoginSuccessfully() {
        givenOldUserWithJwtSubject()
            .whenUserLogin()
            .thenLoginSuccessfully()
    }

    private fun givenInvalidJwtSubject(): String? = null

    private fun givenNewUserWithJwtSubject(): String {
        val subject = "google-oauth2|102527320242660434908"
        assertThat(userRepository.existsByIdentitiesIn(subject)).isFalse()
        return subject
    }

    private fun givenOldUserWithJwtSubject(): String {
        val subject = givenNewUserWithJwtSubject()
        userRepository.createUser(User(identities = listOf(subject)))
        assertThat(userRepository.existsByIdentitiesIn(subject)).isTrue()
        return subject
    }

    private fun String.whenUserLogin(): ResultActions =
        mockMvc.perform(get("/").with(jwt().jwt { it.subject(this) }))

    private fun ResultActions.thenShouldLoginFailed() {
        this.andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").value("JWT subject is null"))
    }

    private fun ResultActions.thenLoginSuccessfully() {
        this.andExpect(status().isOk)
    }

    private fun ResultActions.thenCreateUser() {
        thenLoginSuccessfully()
        val subject = "google-oauth2|102527320242660434908"
        assertThat(userRepository.existsByIdentitiesIn(subject)).isTrue()
    }

}
