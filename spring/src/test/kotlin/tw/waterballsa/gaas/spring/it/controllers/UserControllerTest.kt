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
import tw.waterballsa.gaas.spring.controllers.viewmodel.PlatformViewModel
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
    fun givenUserNickname_whenUpdateEnglishNickname_thenShouldUpdateNickname() {
        givenUserNickname()
            .whenUpdateUserSelf(UpdateUserRequest("my nick name"))
            .thenShouldUpdateNickname("my nick name")
    }

    @Test
    fun givenUserNickname_whenUpdateChineseNickname_thenShouldUpdateNickname() {
        givenUserNickname()
            .whenUpdateUserSelf(UpdateUserRequest("周杰倫"))
            .thenShouldUpdateNickname("周杰倫")
    }

    @Test
    fun givenUserNickname_whenUpdateShortNickname_thenShouldUpdateNicknameFailed() {
        givenUserNickname()
            .whenUpdateUserSelf(UpdateUserRequest("abc"))
            .thenShouldUpdateNicknameFailed("too short")
    }

    @Test
    fun givenUserNickname_whenUpdateLongNickname_thenShouldUpdateNicknameFailed() {
        givenUserNickname()
            .whenUpdateUserSelf(UpdateUserRequest("This is a very long nickname"))
            .thenShouldUpdateNicknameFailed("too long")
    }

    @Test
    fun givenUserNickname_whenUpdateDuplicateNickname_thenShouldUpdateNicknameFailed() {
        val userA = givenUserNickname()
        givenAnotherUserNickname()
            .whenUpdateUserSelf(UpdateUserRequest(userA.nickname))
            .thenShouldUpdateNicknameFailed("duplicated")
    }

    private fun givenUserDoesNotLogIn(): User = this.mockUser

    private fun givenUserHasLoggedIn(): User {
        return userRepository.createUser(mockUser)
    }

    private fun givenUserNickname(): User {
        val user = User(User.Id("1"), "userA@example.com", "Neverever", mockUser.identities)
        return userRepository.createUser(user)
    }

    private fun givenAnotherUserNickname(): User {
        val user = User(User.Id("2"), "userB@example.com", "周杰倫", mockUser.identities)
        return userRepository.createUser(user)
    }

    private fun User.whenGetUserSelf(): ResultActions {
        return mockMvc.perform(get("/users/me").withJwt(toJwt()))
    }

    private fun User.whenUpdateUserSelf(updateUserRequest: UpdateUserRequest): ResultActions =
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

    private fun ResultActions.thenShouldUpdateNickname(nickname: String) {
        val userViewModel = andExpect(status().isOk)
            .getBody(UpdateUserViewModel::class.java)

        userRepository.findById(userViewModel.id)
            .also { assertThat(it).isNotNull }
            .also { assertThat(it!!.nickname).isEqualTo(nickname) }
    }

    private fun ResultActions.thenShouldUpdateNicknameFailed(message: String) {
        andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
            .getBody(PlatformViewModel::class.java)
            .also { assertThat(it.message).contains(message) }
    }

}
