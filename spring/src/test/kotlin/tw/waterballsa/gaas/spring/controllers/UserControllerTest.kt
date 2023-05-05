package tw.waterballsa.gaas.spring.controllers

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User

@SpringBootTest
@ActiveProfiles(profiles = ["dev"])
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    fun givenUserCreated_whenGetUser_thenGetUserSuccessfully() {
        val user = User(User.UserId("1"), "test@mail.com", "")
        givenUserCreated(user)
        val resultActions = whenGetUser("1")
        thenGetUserSuccessfully(resultActions, "1")
    }

    @Test
    fun givenUserNotCreated_whenGetUser_thenUserNotFound() {
        val resultActions = whenGetUser("0")
        thenUserNotFound(resultActions)
    }

    private fun givenUserCreated(user: User) {
        userRepository.createUser(user)
    }

    private fun whenGetUser(id: String): ResultActions = mockMvc.perform(get("/users/$id"))

    private fun thenGetUserSuccessfully(resultActions: ResultActions, id: String) {
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.email").exists())
    }

    private fun thenUserNotFound(resultActions: ResultActions) {
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").doesNotExist())
            .andExpect(jsonPath("$.email").doesNotExist())
    }

}