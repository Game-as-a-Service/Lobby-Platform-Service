package tw.waterballsa.gaas.spring.it.controllers

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest
import tw.waterballsa.gaas.spring.models.TestCreateRoomRequest
import java.time.Instant.now


class RoomControllerTest @Autowired constructor(
    val userRepository: UserRepository,
    val roomRepository: RoomRepository,
    val gameRegistrationRepository: GameRegistrationRepository
) : AbstractSpringBootTest() {

    lateinit var testUser: User
    lateinit var testGame: GameRegistration

    @BeforeEach
    fun setUp() {
        testUser = createUser()
        testGame = registerGame()
    }

    @AfterEach
    fun cleanUp() {
        roomRepository.deleteAll()
    }

    @Test
    fun givenUserIsInTheLobby_WhenUserCreateARoom_ThenShouldSucceed() {
        val request = createRoomRequest()
        createRoom(request)
            .thenCreateRoomSuccessfully(request)
    }

    @Test
    fun givenUserIsInTheLobby_WhenUserCreateARoomWithValidPassword_ThenShouldSucceed() {
        val request = createRoomRequest("1234")
        createRoom(request)
            .thenCreateRoomSuccessfully(request)
    }

    @Test
    fun givenUserIsInTheLobby_WhenUserCreateARoomWithInValidPassword_ThenShouldFail() {
        createRoom(createRoomRequest("12345"))
            .andExpect(status().isBadRequest)

        createRoom(createRoomRequest("abcd"))
            .andExpect(status().isBadRequest)

        createRoom(createRoomRequest("1a2b"))
            .andExpect(status().isBadRequest)

        createRoom(createRoomRequest("qaz"))
            .andExpect(status().isBadRequest)
    }


    @Test
    fun givenUserAlreadyCreatedARoom_WhenUserCreateAnotherRoom_ThenShouldFail() {
        val request = createRoomRequest("1234")
        createRoom(request)
            .thenCreateRoomSuccessfully(request)

        createRoom(request)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").value("A user can only create one room at a time."))
    }

    private fun createUser(): User =
        userRepository.createUser(User(User.Id("1"), "test@mail.com", "winner5566"))

    private fun registerGame(): GameRegistration = gameRegistrationRepository.registerGame(
        GameRegistration(
            uniqueName = "Mahjong-python",
            displayName = "麻將-Python",
            shortDescription = "A simple game.",
            rule = "Follow the rules to win.",
            imageUrl = "https://example.com/game01.jpg",
            minPlayers = 2,
            maxPlayers = 4,
            frontEndUrl = "https://example.com/play/game01",
            backEndUrl = "https://example.com/api/game01"
        )
    )

    private fun createRoomRequest(password: String? = null): TestCreateRoomRequest =
        TestCreateRoomRequest(
            name = "Rapid Mahjong Room",
            gameId = testGame.id!!.value,
            password = password,
            maxPlayers = testGame.maxPlayers,
            minPlayers = testGame.minPlayers,
        )

    private fun createRoom(request: TestCreateRoomRequest): ResultActions =
        mockMvc.perform(
            post("/rooms")
                .with(oidcLogin().oidcUser(mockOidcUser()))
                .contentType(APPLICATION_JSON)
                .content(request.toJson())
        )

    private fun ResultActions.thenCreateRoomSuccessfully(request: TestCreateRoomRequest) {
        request.let {
            this.andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(it.name))
                .andExpect(jsonPath("$.game.id").value(testGame.id!!.value))
                .andExpect(jsonPath("$.game.name").value(testGame.displayName))
                .andExpect(jsonPath("$.host.id").value(testUser.id!!.value))
                .andExpect(jsonPath("$.host.nickname").value(testUser.nickname))
                .andExpect(jsonPath("$.isLocked").value(!it.password.isNullOrEmpty()))
                .andExpect(jsonPath("$.currentPlayers").value(1))
                .andExpect(jsonPath("$.minPlayers").value(it.minPlayers))
                .andExpect(jsonPath("$.maxPlayers").value(it.maxPlayers))
        }
    }

    private fun mockOidcUser(): OidcUser {
        val claims: Map<String, Any> = testUser.run {
            mapOf(
                "sub" to this.id!!.value,
                "name" to nickname,
                "email" to email
            )
        }
        val idToken = OidcIdToken("token", now(), now().plusSeconds(60), claims)
        return DefaultOidcUser(emptyList(), idToken)
    }
}
