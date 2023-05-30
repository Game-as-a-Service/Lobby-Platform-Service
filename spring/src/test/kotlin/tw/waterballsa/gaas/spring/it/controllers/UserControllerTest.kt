package tw.waterballsa.gaas.spring.it.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.controllers.UpdateUserRequest
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest

@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest @Autowired constructor(
    val userRepository: UserRepository,
) : AbstractSpringBootTest() {

    @BeforeEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    fun givenUserCreated_whenGetUser_thenGetUserSuccessfully() {
        val user = givenUserCreated(User.Id("1"), "test@mail.com")
        findUserById("1").thenGetUserSuccessfully(user)
    }

    @Test
    fun givenUserNotCreated_whenGetUser_thenUserNotFound() {
        findUserById("0").thenUserNotFound()
    }

    @Test
    fun givenNicknameNotSet_whenUpdateEnglishNickname_thenShouldUpdateNickname() {
        givenNicknameNotSet("userA@example.com")
            .whenUpdateUser("my nick name")
            .thenShouldUpdateNickname("my nick name")
    }

    @Test
    fun givenNicknameNotSet_whenUpdateChineseNickname_thenShouldUpdateNickname() {
        givenNicknameNotSet("userA@example.com")
            .whenUpdateUser("周杰倫")
            .thenShouldUpdateNickname("周杰倫")
    }

    @Test
    fun givenNicknameExists_whenUpdateNickname_thenShouldNotUpdateNickname() {
        givenUserCreated(User.Id("2"), "userB@example.com", "Neverever")
        givenUserCreated(User.Id("1"), "userA@example.com", "")
            .whenUpdateUser("Neverever")
            .thenShouldNotUpdateNickname()
    }

    @Test
    fun givenNicknameExists_whenUpdateShortNickname_thenShouldNotUpdateNickname() {
        givenUserCreated(User.Id("1"), "userA@example.com", "Neverever")
            .whenUpdateUser("abc")
            .thenShouldNotUpdateNickname()
    }

    @Test
    fun givenNicknameExists_whenUpdateLongNickname_thenShouldNotUpdateNickname() {
        givenUserCreated(User.Id("1"), "userA@example.com", "Neverever")
            .whenUpdateUser("This is a very  long nickname")
            .thenShouldNotUpdateNickname()
    }

    private fun givenUserCreated(email: String): User {
        return givenUserCreated(null, email)
    }

    private fun givenUserCreated(id: User.Id?, email: String, nickname: String = ""): User {
        val user = User(id, email, nickname)
        return userRepository.createUser(user)
    }

    private fun givenNicknameNotSet(email: String): User {
        val userCreated = givenUserCreated(email)
        assertThat(userCreated.nickname).isEmpty()
        return userCreated
    }

    private fun findUserById(id: String): ResultActions = mockMvc.perform(get("/users/$id"))

    private fun User.whenUpdateUser(nickname: String): ResultActions =
        mockMvc.perform(
            put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(UpdateUserRequest(email, nickname).toJson())
        )

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

    private fun ResultActions.thenShouldUpdateNickname(nickname: String) {
        andExpect(status().isOk)
            .andExpect(jsonPath("$.nickname").value(nickname))
    }

    private fun ResultActions.thenShouldNotUpdateNickname() {
        andExpect(status().isBadRequest)
    }
}
