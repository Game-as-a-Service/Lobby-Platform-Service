package tw.waterballsa.gaas.spring.it.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.controllers.UpdateUserRequest
import tw.waterballsa.gaas.spring.controllers.viewmodel.UpdateUserViewModel
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

    @Test
    fun givenUserNamedNeverever_whenChangeUserNicknameToMyNickName_thenUserNicknameShouldBeMyNickName() {
        givenUserNickname("Neverever")
            .whenChangeUserNickname(UpdateUserRequest("my nick name"))
            .thenUserNicknameShouldBeChanged("my nick name")
    }

    @Test
    fun givenUserNamedNeverever_whenChangeUserNicknameTo周杰倫_thenUserNicknameShouldBe周杰倫() {
        givenUserNickname("Neverever")
            .whenChangeUserNickname(UpdateUserRequest("周杰倫"))
            .thenUserNicknameShouldBeChanged("周杰倫")
    }

    @Test
    fun givenUserNamedNeverever_whenChangeUserNicknameTooShort_thenShouldChangeNicknameFailed() {
        givenUserNickname("Neverever")
            .whenChangeUserNickname(UpdateUserRequest("abc"))
            .thenShouldChangeNicknameFailed("invalid nickname: too short")
    }

    @Test
    fun givenUserNamedNeverever_whenChangeUserNicknameTooLong_thenShouldChangeNicknameFailed() {
        givenUserNickname("Neverever")
            .whenChangeUserNickname(UpdateUserRequest("This is a very long nickname"))
            .thenShouldChangeNicknameFailed("invalid nickname: too long")
    }

    @Test
    fun givenUserNamedNeverever_whenAnotherUserChangeToNeverever_thenShouldChangeNicknameFailed() {
        givenUserNickname("Neverever")
        givenAnotherUserNickname("周杰倫")
            .whenChangeUserNickname(UpdateUserRequest("Neverever"))
            .thenShouldChangeNicknameFailed("invalid nickname: duplicated")
    }

    private fun givenUserDoesNotLogIn(): User = this.mockUser

    private fun givenUserHasLoggedIn(): User {
        return userRepository.createUser(mockUser)
    }

    private fun givenUserNickname(nickname: String): User {
        val user = User("userA@example.com", nickname, mockUser.identities)
        return userRepository.createUser(user)
    }

    private fun givenAnotherUserNickname(nickname: String): User {
        val user = User("userB@example.com", nickname, mockUser.identities)
        return userRepository.createUser(user)
    }

    private fun User.whenGetUserSelf(): ResultActions {
        return mockMvc.perform(get("/users/me").withJwt(toJwt()))
    }

    private fun User.whenChangeUserNickname(updateUserRequest: UpdateUserRequest): ResultActions =
        mockMvc.perform(
            put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserRequest.toJson())
                .withJwt(toJwt())
        )

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

    private fun ResultActions.thenUserNicknameShouldBeChanged(nickname: String) {
        val user = andExpect(status().isOk)
            .getBody(UpdateUserViewModel::class.java)

        userRepository.findById(user.id)
            .also { assertThat(it).isNotNull }
            .also { assertThat(it!!.nickname).isEqualTo(nickname) }
    }

    private fun ResultActions.thenShouldChangeNicknameFailed(message: String) {
        andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value(message))
    }

}
