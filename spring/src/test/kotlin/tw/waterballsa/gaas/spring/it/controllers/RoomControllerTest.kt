package tw.waterballsa.gaas.spring.it.controllers

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
import tw.waterballsa.gaas.spring.models.TestJoinRoomRequest

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
    lateinit var testRoom: Room

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
    fun giveHasPublicRoomAndHostIsUserAAndUerBIsInTheLobby_WhenUserBJoinThePublicRoom_ThenShouldSucceed(){
        testRoom = createRoom()
        val user = userRepository.createUser(User(User.Id("2"), "test2@mail.com", "winner1122"))
        val request = joinRoomRequest(user.id!!.value);
        joinRoom(request)
            .thenJoinRoomSuccessfully(request)
    }

    @Test
    fun giveHasEncryptedRoomAndHostIsUserAAndUerBIsInTheLobby_WhenUserBJoinTheEncryptedRoomAndThePasswordIsIncorrect_ThenShouldFail(){
        val password = "P@ssw0rd"
        val errorPassword = "password"
        testRoom = createRoom(password)
        val user = userRepository.createUser(User(User.Id("2"), "test2@mail.com", "winner1122"))
        val request = joinRoomRequest(user.id!!.value, errorPassword);
        joinRoom(request)
            .andExpect(status().isBadRequest)
    }
    @Test
    fun giveHasEncryptedRoomAndHostIsUserAAndUerBIsInTheLobby_WhenUserBJoinTheEncryptedRoomAndThePasswordIsCorrect_ThenShouldSucceed(){
        val password = "P@ssw0rd"
        testRoom = createRoom(password)
        val user = userRepository.createUser(User(User.Id("2"), "test2@mail.com", "winner1122"))
        val request = joinRoomRequest(user.id!!.value, password);
        joinRoom(request)
            .thenJoinRoomSuccessfully(request)
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

    private fun joinRoomRequest(userId: String, password: String? = null): TestJoinRoomRequest =
        TestJoinRoomRequest(
            userId = userId,
            password = password
        )

    private fun joinRoom(request: TestJoinRoomRequest): ResultActions =
        mockMvc.perform(post("/rooms/${testRoom.id!!.value}/players").contentType(APPLICATION_JSON).content(request.toJson()))

    private fun ResultActions.thenJoinRoomSuccessfully(request: TestJoinRoomRequest) {
        request.let {
            this.andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(testRoom.id!!.value))
                .andExpect(jsonPath("$.gameRegistrationId").value(testRoom.gameRegistration.id!!.value))
                .andExpect(jsonPath("$.hostId").value(testRoom.host.id!!.value))
                .andExpect(jsonPath("$.hostName").value(testRoom.host.nickname))
                .andExpect(jsonPath("$.playerIds").exists())
                .andExpect(jsonPath("$.playerIds").isArray)
                .andExpect(jsonPath("$.playerIds[0]").value(testRoom.players[0].id!!.value))
                .andExpect(jsonPath("$.playerIds[1]").value(it.userId))
                .andExpect(jsonPath("$.maxPlayers").value(testRoom.maxPlayers))
                .andExpect(jsonPath("$.minPlayers").value(testRoom.minPlayers))
                .andExpect(jsonPath("$.name").value(testRoom.name))
                .andExpect(jsonPath("$.status").value(Room.Status.WAITING.name))
        }
    }

    private fun createRoom(password: String? = null): Room = roomRepository.createRoom(
        Room(
            gameRegistration = testGame,
            host = Room.Player(User.Id(testUser.id!!.value), testUser.nickname),
            players =  mutableListOf (Room.Player(User.Id(testUser.id!!.value), testUser.nickname)),
            maxPlayers = testGame.maxPlayers,
            minPlayers = testGame.minPlayers,
            name = "My Room",
            status = Room.Status.WAITING,
            password = password
        )
    )
}