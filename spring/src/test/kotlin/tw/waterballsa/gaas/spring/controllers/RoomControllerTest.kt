package tw.waterballsa.gaas.spring.controllers

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.model.TestCreateRoomRequest

@SpringBootTest
@ActiveProfiles(profiles = ["dev"])
@AutoConfigureMockMvc(addFilters = false)
class RoomControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val roomRepository: RoomRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val gameRegistrationRepository: GameRegistrationRepository
) {

    @MockBean
    lateinit var eventBus: EventBus

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
    fun givenUserIsInTheLobby_whenUserCreateARoom_ThenShouldSucceed() {
        val request = createRoomRequest("1234")

        createRoom(request)
            .thenCreateRoomSuccessfully(request)
    }

    @Test
    fun givenUserHasCreatedARoom_WhenUserCreateAnotherRoom_ThenShouldFail() {
        val request = createRoomRequest()
        createRoom(request)
            .thenCreateRoomSuccessfully(request)

        createRoom(request)
            .andExpect(status().isBadRequest)
    }

    private fun createRoomRequest(password: String? = null): TestCreateRoomRequest =
        TestCreateRoomRequest(
            gameId = testGame.id!!.value,
            hostId = testUser.id!!.value,
            maxPlayers = testGame.maxPlayers,
            minPlayers = testGame.minPlayers,
            name = "My Room",
            description = "This is a test room.",
            password = password
        )

    private fun createRoom(request: TestCreateRoomRequest): ResultActions =
        mockMvc.perform(post("/rooms").contentType(APPLICATION_JSON).content(request.toJson()))

    private fun ResultActions.thenCreateRoomSuccessfully(request: TestCreateRoomRequest) {
        request.let {
            this.andExpect(status().isOk)
                .andExpect(jsonPath("$.roomId").exists())
                .andExpect(jsonPath("$.gameRegistrationId").value(testGame.id))
                .andExpect(jsonPath("$.hostId").value(testUser.id))
                .andExpect(jsonPath("$.hostName").value(testUser.nickname))
                .andExpect(jsonPath("$.playerIds").exists())
                .andExpect(jsonPath("$.playerIds").isArray)
                .andExpect(jsonPath("$.playerIds[0]").value(testUser.id))
                .andExpect(jsonPath("$.maxPlayers").value(it.maxPlayers))
                .andExpect(jsonPath("$.minPlayers").value(it.minPlayers))
                .andExpect(jsonPath("$.name").value(it.name))
                .andExpect(jsonPath("$.description").value(it.description))
                .andExpect(jsonPath("$.status").value(Room.Status.WAITING.name))
                .andExpect(jsonPath("$.isEncrypted").value(!request.password.isNullOrEmpty()))
        }
    }

    private fun createUser(): User =
        userRepository.createUser(User(User.UserId("1"), "test@mail.com", "winner5566"))

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
}
