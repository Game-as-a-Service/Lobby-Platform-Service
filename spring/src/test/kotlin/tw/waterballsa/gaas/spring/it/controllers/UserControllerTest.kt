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
    fun givenUserCreated_whenGetUserMe_thenShouldReturnUserInfo() {
        givenUserCreated()
            .whenGetUserMe()
            .thenGetUserSuccessfully()
    }

    @Test
    fun givenUserNotCreated_whenGetUserMe_thenUserNotFound() {
        givenUserNotCreated()
            .whenGetUserMe()
            .thenUserNotFound()
    }

    private fun givenUserNotCreated(): User = this.mockUser

    private fun givenUserCreated(): User {
        return userRepository.createUser(mockUser)
    }

    private fun User.whenGetUserMe(): ResultActions {
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
