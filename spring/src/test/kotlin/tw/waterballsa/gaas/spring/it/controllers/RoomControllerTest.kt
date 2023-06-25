package tw.waterballsa.gaas.spring.it.controllers

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.Room.Player
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest
import tw.waterballsa.gaas.spring.models.TestCreateRoomRequest
import tw.waterballsa.gaas.spring.models.TestGetRoomsRequest
import tw.waterballsa.gaas.spring.models.TestJoinRoomRequest
import java.time.Instant.now


class RoomControllerTest @Autowired constructor(
    val userRepository: UserRepository,
    val roomRepository: RoomRepository,
    val gameRegistrationRepository: GameRegistrationRepository
) : AbstractSpringBootTest() {

    lateinit var testUser: User
    lateinit var testGame: GameRegistration
    lateinit var testRoom: Room

    @BeforeEach
    fun setUp() {
        testUser = createUser("1", "test@mail.com", "winner5566")
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
            .andExpect(jsonPath("$.message").value("A user can only create one room at a time."))
    }

    @Test
    fun giveUserACreatedRoomC_WhenUserBJoinRoomC_ThenShouldSucceed() {
        val userA = testUser
        val userB = createUser("2", "test2@mail.com", "winner1122")
        givenTheHostCreatePublicRoom(userA)
            .whenUserJoinTheRoom(userB)
            .thenJoinRoomSuccessfully()
    }

    @Test
    fun giveUserACreatedRoomCWithPassword_WhenUserBJoinRoomCWithIncorrectPassword_ThenShouldFail() {
        val password = "P@ssw0rd"
        val errorPassword = "password"
        val userA = testUser
        val userB = createUser("2", "test2@mail.com", "winner1122")
        givenTheHostCreateRoomWithPassword(userA, password)
            .whenUserJoinTheRoom(userB, errorPassword)
            .thenJoinRoomFailed()
    }

    @Test
    fun giveUserACreatedRoomCWithPassword_WhenUserBJoinRoomCWithCorrectPassword_ThenShouldSucceed() {
        val password = "P@ssw0rd"
        val userA = testUser
        val userB = createUser("2", "test2@mail.com", "winner1122")
        givenTheHostCreateRoomWithPassword(userA, password)
            .whenUserJoinTheRoom(userB, password)
            .thenJoinRoomSuccessfully()
    }

    @Test
    fun givenWaitingRoomBAndWaitingRoomC_WhenUserAVisitLobby_ThenShouldHaveRoomBAndRoomC() {
        val userA = testUser
        val userB = createUser("2", "test2@mail.com", "winner1122")
        val userC = createUser("3", "test3@mail.com", "winner1234")
        val request = TestGetRoomsRequest("WAITING", 0, 10)

        givenWaitingRooms(userB, userC)
        request.whenUserAVisitLobby(userA)
            .thenShouldHaveRooms(request)
    }

    @Test
    fun givenExistingRoom_RoomOwnerCloseRoom_ShouldSuccess() {
        val host = testUser
        val room = givenTheHostCreatePublicRoom(host)

        mockMvc.perform(
            delete("/rooms/${room.roomId!!.value}")
                .with(oidcLogin().oidcUser(mockOidcUser(host)))
        ).andExpect(status().isNoContent)
    }

    @Test
    fun givenExistingRoom_NonRoomOwnerCloseRoom_ShouldFail() {
        val host = testUser
        val room = givenTheHostCreatePublicRoom(host)
        val userA = createUser("2", "test2@mail.com", "not_a_room_owner")

        mockMvc.perform(
            delete("/rooms/${room.roomId!!.value}")
                .with(oidcLogin().oidcUser(mockOidcUser(userA)))
        ).andExpect(status().isBadRequest)
    }

    private fun TestGetRoomsRequest.whenUserAVisitLobby(joinUser: User): ResultActions =
        mockMvc.perform(
            get("/rooms")
                .with(oidcLogin().oidcUser(mockOidcUser(joinUser)))
                .param("status", status)
                .param("page", page.toString())
                .param("offset", offset.toString())
        )

    private fun givenWaitingRooms(vararg users: User) =
        users.forEach { givenTheHostCreatePublicRoom(it) }

    private fun ResultActions.thenShouldHaveRooms(request: TestGetRoomsRequest) {
        val rooms = roomRepository.findByStatus(request.toStatus(), request.toPagination())
        andExpect(status().isOk)
            .andExpect(jsonPath("$.rooms").isArray)
            .andExpect(jsonPath("$.rooms.length()").value(rooms.data.size))
            .roomExcept(rooms)
    }

    private fun ResultActions.roomExcept(rooms: Pagination<Room>) {
        rooms.data.forEachIndexed() { index, room ->
            andExpect(jsonPath("$.rooms[$index].id").value(room.roomId!!.value))
                .andExpect(jsonPath("$.rooms[$index].name").value(room.name))
                .andExpect(jsonPath("$.rooms[$index].game.id").value(room.game.id!!.value))
                .andExpect(jsonPath("$.rooms[$index].host.id").value(room.host.id.value))
                .andExpect(jsonPath("$.rooms[$index].isLocked").value(room.isLocked))
                .andExpect(jsonPath("$.rooms[$index].currentPlayers").value(room.players.size))
                .andExpect(jsonPath("$.rooms[$index].maxPlayers").value(room.maxPlayers))
                .andExpect(jsonPath("$.rooms[$index].minPlayers").value(room.minPlayers))
        }
    }

    private fun createRoom(request: TestCreateRoomRequest): ResultActions =
        mockMvc.perform(
            post("/rooms")
                .with(oidcLogin().oidcUser(mockOidcUser(testUser)))
                .withJson(request)
        )

    private fun joinRoom(request: TestJoinRoomRequest, joinUser: OidcUser): ResultActions =
        mockMvc.perform(
            post("/rooms/${testRoom.roomId!!.value}/players")
                .with(oidcLogin().oidcUser(joinUser))
                .withJson(request)
        )

    private fun givenTheHostCreatePublicRoom(host: User): Room {
        testRoom = createRoom(host)
        return testRoom
    }

    private fun givenTheHostCreateRoomWithPassword(host: User, password: String): Room {
        testRoom = createRoom(host, password)
        return testRoom
    }

    private fun Room.whenUserJoinTheRoom(user: User, password: String? = null): ResultActions {
        val request = joinRoomRequest(password)
        val joinUser = mockOidcUser(user)
        return joinRoom(request, joinUser)
    }

    private fun ResultActions.thenCreateRoomSuccessfully(request: TestCreateRoomRequest) {
        request.let {
            andExpect(status().isCreated)
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

    private fun ResultActions.thenJoinRoomSuccessfully() {
        andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("success"))
    }

    private fun ResultActions.thenJoinRoomFailed() {
        andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("wrong password"))
    }

    private fun createUser(id: String, email: String, nickname: String): User =
        userRepository.createUser(User(User.Id(id), email, nickname))

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

    private fun createRoom(host: User, password: String? = null): Room = roomRepository.createRoom(
        Room(
            game = testGame,
            host = Player(Player.Id(host.id!!.value), host.nickname),
            players = mutableListOf(Player(Player.Id(host.id!!.value), host.nickname)),
            maxPlayers = testGame.maxPlayers,
            minPlayers = testGame.minPlayers,
            name = "My Room",
            status = Room.Status.WAITING,
            password = password
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

    private fun mockOidcUser(user: User): OidcUser {
        val claims: Map<String, Any> =
            mapOf(
                "sub" to user.id!!.value,
                "name" to user.nickname,
                "email" to user.email
            )

        val idToken = OidcIdToken("token", now(), now().plusSeconds(60), claims)
        return DefaultOidcUser(emptyList(), idToken)
    }

    private fun joinRoomRequest(password: String? = null): TestJoinRoomRequest =
        TestJoinRoomRequest(
            password = password
        )
}
