package tw.waterballsa.gaas.spring.controllers

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import tw.waterballsa.gaas.application.eventbus.EventBus
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

    @MockBean
    lateinit var eventBus: EventBus

    @BeforeEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    fun givenUserCreated_whenGetUser_thenGetUserSuccessfully() {
        val user = User(User.UserId("1"), "test@mail.com", "winner5566")
        givenUserCreated(user)
        findUserById("1").thenGetUserSuccessfully(user)
    }

    @Test
    fun givenUserNotCreated_whenGetUser_thenUserNotFound() {
        findUserById("0").thenUserNotFound()
    }

    private fun givenUserCreated(user: User) {
        userRepository.createUser(user)
    }

    private fun findUserById(id: String): ResultActions = mockMvc.perform(get("/users/$id"))

    private fun ResultActions.thenGetUserSuccessfully(user: User) {
        this.andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(user.id!!.value))
            .andExpect(jsonPath("$.email").value(user.email))
            .andExpect(jsonPath("$.nickname").value(user.nickname))
    }

    private fun ResultActions.thenUserNotFound() {
        this.andExpect(status().isNotFound)
            .andExpect(jsonPath("$.id").doesNotExist())
            .andExpect(jsonPath("$.email").doesNotExist())
            .andExpect(jsonPath("$.nickname").doesNotExist())
    }

}