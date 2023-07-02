package tw.waterballsa.gaas.spring.it.controllers

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest

class UserControllerTest @Autowired constructor(
    val userRepository: UserRepository,
) : AbstractSpringBootTest() {

    @BeforeEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    fun givenUserHasLoggedIn_whenGetUserSelf_thenGetUserSuccessfully() {
        givenUserHasLoggedIn()
            .whenGetUserSelf()
            .thenGetUserSuccessfully()
    }

    @Test
    fun givenUserDoesNotLogIn_whenGetUserSelf_thenUserNotFound() {
        givenUserDoesNotLogIn()
            .whenGetUserSelf()
            .thenUserNotFound()
    }

    private fun givenUserDoesNotLogIn(): User = this.mockUser

    private fun givenUserHasLoggedIn(): User {
        return userRepository.createUser(mockUser)
    }

    private fun User.whenGetUserSelf(): ResultActions {
        val jwt = identities.first().toJwt()
        return mockMvc.perform(get("/users/me").withJwt(jwt))
    }

    private fun ResultActions.thenGetUserSuccessfully() {
        this.andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(mockUser.id!!.value))
            .andExpect(jsonPath("$.email").value(mockUser.email))
            .andExpect(jsonPath("$.nickname").value(mockUser.nickname))
    }

    private fun ResultActions.thenUserNotFound() {
        this.andExpect(status().isNotFound)
            .andExpect(jsonPath("$.id").doesNotExist())
            .andExpect(jsonPath("$.email").doesNotExist())
            .andExpect(jsonPath("$.nickname").doesNotExist())
    }

}
