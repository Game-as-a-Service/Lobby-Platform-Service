package tw.waterballsa.gaas.spring.it.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.stubbing.OngoingStubbing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.client.GameService
import tw.waterballsa.gaas.application.client.StartGameRequest
import tw.waterballsa.gaas.application.client.StartGameResponse
import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.Room.Player
import tw.waterballsa.gaas.domain.Room.Status
import tw.waterballsa.gaas.domain.Room.Status.PLAYING
import tw.waterballsa.gaas.domain.Room.Status.WAITING
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.GAME_START_FAILED
import tw.waterballsa.gaas.spring.controllers.RoomController.FastJoinRoomRequest
import tw.waterballsa.gaas.spring.controllers.presenter.CreateRoomViewModel
import tw.waterballsa.gaas.spring.controllers.viewmodel.FastJoinRoomViewModel
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest
import tw.waterballsa.gaas.spring.models.TestCreateRoomRequest
import tw.waterballsa.gaas.spring.models.TestGetRoomsRequest
import tw.waterballsa.gaas.spring.models.TestJoinRoomRequest
import tw.waterballsa.gaas.spring.utils.MockitoUtils.Companion.anyObject
import tw.waterballsa.gaas.spring.utils.Users.Companion.defaultUser
import java.time.Instant
import java.util.UUID.randomUUID
import kotlin.reflect.KClass

class RoomControllerTest @Autowired constructor(
    val userRepository: UserRepository,
    val roomRepository: RoomRepository,
    val gameRegistrationRepository: GameRegistrationRepository,
) : AbstractSpringBootTest() {

    lateinit var testUser: User
    lateinit var testGame: GameRegistration
    lateinit var testRoom: Room

    @MockBean
    lateinit var gameService: GameService

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
        roomRepository.deleteAll()

        testUser = mockUser.createUser()
        testGame = registerGame()
    }

    @Test
    fun givenUserIsInTheLobby_WhenUserCreateARoom_ThenShouldSucceed() {
        val request = createRoomRequest()
        createRoom(testUser, request)
            .thenCreateRoomSuccessfully()
    }

    @Test
    fun givenUserIsInTheLobby_WhenUserCreateARoomWithValidPassword_ThenShouldSucceed() {
        val request = createRoomRequest("1234")
        createRoom(testUser, request)
            .thenCreateRoomSuccessfully()
    }

    @Test
    fun givenUserIsInTheLobby_WhenUserCreateARoomWithInValidPassword_ThenShouldFail() {
        createRoom(testUser, createRoomRequest("12345"))
            .andExpect(status().isBadRequest)

        createRoom(testUser, createRoomRequest("abcd"))
            .andExpect(status().isBadRequest)

        createRoom(testUser, createRoomRequest("1a2b"))
            .andExpect(status().isBadRequest)

        createRoom(testUser, createRoomRequest("qaz"))
            .andExpect(status().isBadRequest)
    }


    @Test
    fun givenUserAlreadyCreatedARoom_WhenUserCreateAnotherRoom_ThenShouldFail() {
        val request = createRoomRequest("1234")
        createRoom(testUser, request)
            .thenCreateRoomSuccessfully()
        createRoom(testUser, request)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("A user can only create one room or join one room at a time."))
    }

    @Test
    fun givenUserACreatedRoomC_WhenUserBJoinRoomC_ThenShouldSucceed() {
        val userA = testUser
        val userB = defaultUser("2").createUser()

        givenTheHostCreatePublicRoom(userA)
            .whenUserJoinTheRoom(userB)
            .thenActionSuccessfully()
    }

    @Test
    fun givenUserACreatedRoomCWithPassword_WhenUserBJoinRoomCWithIncorrectPassword_ThenShouldFail() {
        val password = "P@ssw0rd"
        val errorPassword = "password"
        val userA = testUser
        val userB = defaultUser("2").createUser()

        givenTheHostCreateRoomWithPassword(userA, password)
            .whenUserJoinTheRoom(userB, errorPassword)
            .thenShouldFail("wrong password")
    }

    @Test
    fun givenUserACreatedRoomCWithPassword_WhenUserBJoinRoomCWithCorrectPassword_ThenShouldSucceed() {
        val password = "P@ssw0rd"
        val userA = testUser
        val userB = defaultUser("2").createUser()

        givenTheHostCreateRoomWithPassword(userA, password)
            .whenUserJoinTheRoom(userB, password)
            .thenActionSuccessfully()
    }

    @Test
    fun givenWaitingRoomBAndWaitingRoomC_WhenUserAVisitLobby_ThenShouldHaveRoomBAndRoomC() {
        val userA = testUser
        val userB = defaultUser("2").createUser()
        val userC = defaultUser("3").createUser()
        val request = TestGetRoomsRequest("WAITING", null, null, 0, 10)

        givenWaitingRooms(userB, userC)
        request.whenUserAVisitLobby(userA)
            .thenShouldHaveRooms(request)
    }

    @Test
    fun givenRoomIsNotFull_whenUserJoinRoom_ThenShouldSucceed() {
        val userA = testUser
        val userB = defaultUser("2").createUser()
        givenTheHostCreatePublicRoom(userA)
            .whenUserJoinTheRoom(userB)
            .thenActionSuccessfully()
    }

    @Test
    fun givenRoomIsFull_whenUserJoinRoom_ThenShouldFail() {
        val host = testUser
        val userE = defaultUser("5").createUser()

        givenTheHostCreatePublicRoomWithUsers(host, "2", "3", "4")
            .whenUserJoinTheRoom(userE)
            .andExpect(status().isBadRequest)
    }

    @Test
    fun givenPlayerAIsNotReadyInRoomA_WhenPlayerAGetReady_ThenShouldPlayerABeReady() {
        val userA = testUser
        val roomA = createRoom(userA)

        whenUserGetReadyFor(roomA.roomId!!, userA)
            .thenActionSuccessfully()

        assertRoomPlayerGetReady(roomA, userA)
    }

    @Test
    fun givenPlayerAIsNotInRoom_WhenPlayerAGetReady_ThenShouldFail() {
        val userA = testUser
        val userB = defaultUser("2").createUser()
        val roomB = createRoom(userB)

        whenUserGetReadyFor(roomB.roomId!!, userA)
            .thenShouldFail("Player not joined")
    }

    @Test
    fun givenPlayerAAndNoRoomsInTheLobby_WhenUserAGetReady_ThenShouldFail() {
        val userA = testUser
        val notExistsRoomId = Room.Id("not_exists_room")

        whenUserGetReadyFor(notExistsRoomId, userA)
            .thenShouldBeNotFound(Room::class)
    }

    @Test
    fun givenPlayerAIsReadyInRoomA_WhenPlayerACancelReady_ThenShouldPlayerABeUnready() {
        val userA = testUser
        val roomA = createRoom(host = userA, hostReady = true)

        whenUserCancelReadyFor(roomA.roomId!!, userA)
            .thenActionSuccessfully()

        assertRoomPlayerNotReady(roomA, userA)
    }

    @Test
    fun givenPlayerAIsNotInRoom_WhenPlayerACancelReady_ThenShouldFail() {
        val userA = testUser
        val userB = defaultUser("2").createUser()
        val roomB = createRoom(userB)

        whenUserCancelReadyFor(roomB.roomId!!, userA)
            .thenShouldFail("Player not joined")
    }

    @Test
    fun givenPlayerAAndNoRoomsInLobby_WhenPlayerACancelReady_ThenShouldFail() {
        val userA = testUser
        val roomId = Room.Id("not-exist-room-id")

        whenUserCancelReadyFor(roomId, userA)
            .thenShouldBeNotFound(Room::class)
    }

    @Test
    fun givenHostCreatedRoom_whenHostCloseRoom_ShouldSuccess() {
        val host = testUser
        val room = givenTheHostCreatePublicRoom(host)

        deleteRoom(host, room.roomId!!.value)
            .andExpect(status().isNoContent)
    }

    @Test
    fun givenHostCreatedRoom_whenNonHostPlayerCloseRoom_ShouldFail() {
        val host = testUser
        val room = givenTheHostCreatePublicRoom(host)
        val userA = defaultUser("2").createUser()

        deleteRoom(userA, room.roomId!!.value)
            .andExpect(status().isBadRequest)
    }

    @Test
    fun givenHostACreatedRoomCAndPlayerBJoined_whenHostAKickPlayerB_thenHostAShouldInTheRoomC() {
        val hostA = testUser
        val playerB = defaultUser("2").createUser()
        val roomC = givenHostCreatedRoomAndPlayerJoined(hostA, playerB)

        thenPlayersShouldBeInTheRoom(roomC.roomId!!, hostA)
    }

    @Test
    fun givenHostACreatedRoomC_whenHostAKickPlayerB_thenHostAShouldInTheRoomC() {
        val hostA = testUser
        val playerB = defaultUser("2").createUser()
        val roomC = givenTheHostCreatePublicRoom(hostA)

        thenPlayersShouldBeInTheRoom(roomC.roomId!!, hostA)
    }

    @Test
    fun givenHostACreatedRoomCAndPlayerBJoined_whenHostBKickPlayerA_thenHostAAndPlayerBShouldInTheRoomC() {
        val hostA = testUser
        val playerB = defaultUser("2").createUser()
        val roomC = givenHostCreatedRoomAndPlayerJoined(hostA, playerB)

        thenPlayersShouldBeInTheRoom(roomC.roomId!!, hostA, playerB)
    }

    @Test
    fun givenHostAndPlayerBAndPlayerCAreInRoomD_WhenHostLeaveRoomD_ThenPreviousHostShouldBeNotInRoomDAndChangedNewHost() {
        val userA = testUser
        val host = userA.toRoomPlayer()
        val playerB = defaultUser("2").createUser().toRoomPlayer()
        val playerC = defaultUser("3").createUser().toRoomPlayer()

        givenHostAndPlayersJoinedTheRoom(host, playerB, playerC)
            .whenUserLeaveTheRoom(userA)
            .thenPlayerShouldBeNotInRoomAndHostIsChanged(host)
    }

    @Test
    @DisplayName(
        """
        Given: Player A has already joined the room B
        When: Player A join another room C
        Then: Should fail
    """
    )
    fun testUserJoinedAnotherRoom() {
        val userA = testUser
        val userB = defaultUser("2").createUser()
        val userC = defaultUser("3").createUser()

        givenTheHostCreatePublicRoom(userA)
            .whenUserJoinTheRoom(userB)
            .thenActionSuccessfully()

        givenTheHostCreatePublicRoom(userC)
            .whenUserJoinTheRoom(userB)
            .thenShouldFail("Player(${userB.id!!.value}) has joined another room.")
    }

    @Test
    fun givenHostAndPlayerBJoinedRoomC_WhenHostGetRoomC_ThenShouldGetRoomCSuccessfully() {
        val userA = testUser
        val host = userA.toRoomPlayer()
        val playerB = defaultUser("2").createUser().toRoomPlayer()

        givenHostAndPlayersJoinedTheRoom(host, playerB)
            .whenUserGetTheRoom(userA)
            .thenGetRoomSuccessfully()
    }

    @Test
    fun givenUserANotJoinedRoomB_WhenUserAGetRoomB_ThenShouldFail() {
        val userA = testUser
        val host = defaultUser("2").createUser().toRoomPlayer()

        givenHostAndPlayersJoinedTheRoom(host)
            .whenUserGetTheRoom(userA)
            .thenShouldFail("Player(${userA.id!!.value}) is not in the room(${testRoom.roomId!!.value}).")
    }

    @Test
    fun givenHostJoinedRoom_WhenHostLeaveRoom_ThenShouldCloseRoom() {
        val userA = testUser
        val host = userA.toRoomPlayer()

        givenHostAndPlayersJoinedTheRoom(host)
            .whenUserLeaveTheRoom(userA)
            .thenShouldCloseRoom()
    }

    @Test
    fun givenRoomHaveFourReadyPlayers_whenHostStartGame_thenGameShouldBeStarted() {
        val host = testUser
        val room = host.givenRoomHaveFourReadyPlayers()

        mockGameService().thenReturn(StartGameResponse("${room.game.frontEndUrl}/games/${room.game.id!!.value}"))

        host.whenHostStartGame(room)
            .thenGameShouldBeStarted(room)
    }

    @Test
    fun givenRoomHaveFourReadyPlayers_whenHostStartGame_thenConnectGameServerFailAndStartGameShouldBeFailed() {
        val failedMessage = "Failed to start game"
        val host = testUser
        val room = host.givenRoomHaveFourReadyPlayers()

        mockGameService().thenThrow(PlatformException(GAME_START_FAILED, failedMessage))

        host.whenHostStartGame(room)
            .thenConnectGameServerFailAndStartGameShouldBeFailed(failedMessage)
    }

    @Test
    fun givenMahjongGameRoom_WhenUserBFastJoinMahjongGameRoom_ThenUserBShouldJoinedRoom() {
        val room = createRoom(testUser)
        val userB = defaultUser("2").createUser()
        FastJoinRoomRequest(testGame.id!!.value)
            .whenFastJoin(userB)
            .thenShouldFastJoinRoom(room.roomId!!, testGame.id!!, Player.Id(userB.id!!.value))
    }

    @Test
    fun givenFullMahjongGameRoom_WhenUserEFastJoinMahjongGameRoom_ThenUserEShouldCreateRoom() {
        val host = testUser

        givenTheHostCreatePublicRoomWithUsers(host, "2", "3", "4")

        val userE = defaultUser("5").createUser()

        FastJoinRoomRequest(testGame.id!!.value)
            .whenFastJoin(userE)
            .thenShouldCreateRoom(gameId = testGame.id!!, playerId = Player.Id(userE.id!!.value))
    }

    @DisplayName(
        """
        Given: Both mahjong game room A and mahjong game room B are created, room A was created earlier than room B
        When: User C faster join Mahjong game room
        Then: User C should join room A
    """
    )
    @Test
    fun testFastJoinEarlyCreatedRoom() {
        val roomA = givenTheHostCreatePublicRoom(testUser)
        givenTheHostCreatePublicRoom(defaultUser("2").createUser())

        val userC = defaultUser("3").createUser()
        FastJoinRoomRequest(testGame.id!!.value)
            .whenFastJoin(userC)
            .thenShouldFastJoinRoom(roomA.roomId!!, testGame.id!!, Player.Id(userC.id!!.value))
    }

    @Test
    fun givenUserBHasJoinedRoom_WhenUserBFastJoin_ThenShouldFailed() {
        givenTheHostCreatePublicRoomWithUsers(testUser, "2")
        val userB = defaultUser("2")

        FastJoinRoomRequest(testGame.id!!.value)
            .whenFastJoin(userB)
            .thenShouldFail("Player(${userB.id!!.value}) has joined another room.")
    }

    @Test
    fun givenHostAndPlayerBArePlayingInRoomC_WhenHostEndGame_ThenRoomCAndPlayersStatusAreChanged() {
        val userA = testUser
        val host = userA.toRoomPlayer()
        val playerB = defaultUser("2").createUser().toRoomPlayer()

        givenPlayersArePlayingInRoom(host, playerB)
            .whenEndGame(userA)
            .thenRoomAndPlayersStatusAreChanged()
    }


    @Test
    fun givenHostAAndPlayerBArePlayingInRoomC_WhenUserDEndGame_ThenShouldFailed() {
        val userA = testUser
        val host = userA.toRoomPlayer()
        val playerB = defaultUser("2").createUser().toRoomPlayer()
        val userD = defaultUser("3").createUser()

        givenPlayersArePlayingInRoom(host, playerB)
            .whenEndGame(userD)
            .thenShouldFail("Player(${userD.id!!.value}) is not in the room(${testRoom.roomId!!.value}).")
    }

    @Test
    fun givenHostAndPlayerBAreWaitingInRoomC_WhenHostEndGame_ThenShouldFailed() {
        val userA = testUser
        val host = userA.toRoomPlayer()
        val playerB = defaultUser("2").createUser().toRoomPlayer()

        givenHostAndPlayersJoinedTheRoom(host, playerB)
            .whenEndGame(userA)
            .thenShouldFail("Game has not started yet")
    }

    private fun TestGetRoomsRequest.whenUserAVisitLobby(joinUser: User): ResultActions =
        mockMvc.perform(
            get("/rooms")
                .withJwt(joinUser.toJwt())
                .param("status", status)
                .param("page", page.toString())
                .param("perPage", perPage.toString())
        )

    private fun givenWaitingRooms(vararg users: User): Unit =
        users.forEach(::givenTheHostCreatePublicRoom)

    private fun ResultActions.thenShouldHaveRooms(request: TestGetRoomsRequest) {
        val rooms = roomRepository.findByQuery(request.toQuery(), request.toPageable())
        andExpect(status().isOk)
            .andExpect(jsonPath("$.rooms").isArray)
            .andExpect(jsonPath("$.rooms.length()").value(rooms.data.size))
            .roomExcept(rooms)
    }

    private fun ResultActions.roomExcept(rooms: Pagination<Room>) {
        rooms.data.forEachIndexed { index, room ->
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

    private fun createRoom(user: User, request: TestCreateRoomRequest): ResultActions =
        mockMvc.perform(
            post("/rooms")
                .withJwt(user.toJwt())
                .withJson(request)
        )

    private fun User.joinRoom(roomId: String, request: TestJoinRoomRequest): ResultActions =
        mockMvc.perform(
            post("/rooms/$roomId/players")
                .withJwt(toJwt())
                .withJson(request)
        )

    private fun deleteRoom(user: User, roomId: String): ResultActions =
        mockMvc.perform(
            delete("/rooms/${roomId}")
                .withJwt(user.toJwt())
        )

    private fun leaveRoom(leaveUser: Jwt): ResultActions =
        mockMvc.perform(
            delete("/rooms/${testRoom.roomId!!.value}/players/me")
                .withJwt(leaveUser)
        )

    private fun FastJoinRoomRequest.whenFastJoin(fastJoinUser: User): ResultActions =
        mockMvc.perform(
            post("/rooms:fastJoin")
                .withJwt(fastJoinUser.toJwt())
                .withJson(this)
        )

    private fun givenTheHostCreatePublicRoom(host: User): Room {
        testRoom = createRoom(host)
        return testRoom
    }

    private fun givenTheHostCreateRoomWithPassword(host: User, password: String): Room {
        testRoom = createRoom(host, password)
        return testRoom
    }

    private fun givenHostAndPlayersJoinedTheRoom(host: Player, vararg players: Player): Room {
        val combinedPlayers = (listOf(host) + players).toMutableList()
        testRoom = createRoom(host, combinedPlayers)
        return testRoom
    }

    private fun givenPlayersArePlayingInRoom(host: Player, vararg players: Player): Room {
        players.forEach { it.ready() }
        val allPlayers = mutableListOf(host, *players)
        testRoom = createRoom(host = host, players = allPlayers, status = PLAYING)
        return testRoom
    }

    private fun Room.whenUserJoinTheRoom(user: User, password: String? = null): ResultActions =
        user.joinRoom(roomId!!.value, joinRoomRequest(password))

    private fun whenUserGetReadyFor(roomId: Room.Id, user: User): ResultActions = mockMvc.perform(
        post("/rooms/${roomId.value}/players/me:ready").withJwt(user.toJwt())
    )

    private fun whenUserCancelReadyFor(roomId: Room.Id, user: User): ResultActions = mockMvc.perform(
        post("/rooms/${roomId.value}/players/me:cancel").withJwt(user.toJwt())
    )

    private fun Room.whenUserLeaveTheRoom(user: User): ResultActions {
        val leaveUser = user.toJwt()
        return leaveRoom(leaveUser)
    }

    private fun Room.whenUserGetTheRoom(user: User) = getRoom(user)

    private fun getRoom(user: User): ResultActions =
        mockMvc.perform(
            get("/rooms/${testRoom.roomId!!.value}")
                .withJwt(user.toJwt())
        )

    private fun Room.whenEndGame(user: User): ResultActions =
        mockMvc.perform(
            post("/rooms/${testRoom.roomId!!.value}:endGame")
                .withJwt(user.toJwt())
        )

    private fun ResultActions.thenCreateRoomSuccessfully() {
        val roomView = getBody(CreateRoomViewModel::class.java)
        val room = roomRepository.findById(roomView.id)!!
        room.let {
            andExpect(status().isCreated)
            assertEquals(roomView.name, it.name)
            assertEquals(roomView.game.id, it.game.id!!.value)
            assertEquals(roomView.game.name, it.game.displayName)
            assertEquals(roomView.host.id, it.host.id.value)
            assertEquals(roomView.host.nickname, it.host.nickname)
            assertEquals(roomView.currentPlayers, it.players.size)
            assertEquals(roomView.minPlayers, it.minPlayers)
            assertEquals(roomView.maxPlayers, it.maxPlayers)
            assertTrue(it.host.readiness)
            assertTrue(it.players.first().readiness)
        }
    }

    private fun ResultActions.thenActionSuccessfully(): ResultActions {
        return andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("success"))
    }

    private fun ResultActions.thenShouldFail(message: String): ResultActions {
        return andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value(message))
    }

    private fun <T : Any> ResultActions.thenShouldBeNotFound(resourceType: KClass<T>) {
        andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("${resourceType.simpleName} not found"))
    }

    private fun ResultActions.thenPlayerShouldBeNotInRoomAndHostIsChanged(player: Player) {
        andExpect(status().isNoContent)
        val room = roomRepository.findById(testRoom.roomId!!)!!
        assertFalse(room.hasPlayer(player.id))
        assertFalse(room.isHost(player.id))
        assertTrue(room.host.readiness)
        assertTrue(room.players.first().readiness)
    }

    private fun ResultActions.thenShouldCloseRoom() {
        andExpect(status().isNoContent)
        val room = roomRepository.findById(testRoom.roomId!!)
        assertNull(room)
    }

    private fun ResultActions.thenShouldFastJoinRoom(
        roomId: Room.Id,
        gameId: GameRegistration.Id,
        playerId: Player.Id
    ) {
        andExpect(status().isOk)
        val fastJoinRoomViewModel = getBody(FastJoinRoomViewModel::class.java)
        val actualRoomId = Room.Id(fastJoinRoomViewModel.roomId)
        assertEquals(roomId, actualRoomId)
        val actualJoinedRoom = roomRepository.findById(actualRoomId)
        assertNotNull(actualJoinedRoom)
        assertEquals(gameId, actualJoinedRoom!!.game.id)
        assertTrue(actualJoinedRoom.hasPlayer(playerId))
    }

    private fun ResultActions.thenShouldCreateRoom(gameId: GameRegistration.Id, playerId: Player.Id) {
        andExpect(status().isOk)
        val fastJoinRoomViewModel = getBody(FastJoinRoomViewModel::class.java)
        val actualRoomId = Room.Id(fastJoinRoomViewModel.roomId)
        val actualJoinedRoom = roomRepository.findById(actualRoomId)
        assertNotNull(actualJoinedRoom)
        assertEquals(gameId, actualJoinedRoom!!.game.id)
        assertEquals(actualJoinedRoom.host.id, playerId)
    }

    private fun givenTheHostCreatePublicRoomWithUsers(host: User, vararg users: String): Room {
        val room = givenTheHostCreatePublicRoom(host)
        users.map { defaultUser(it).createUser() }
            .forEach { room.whenUserJoinTheRoom(it) }
        return room
    }


    private fun User.createUser(): User = userRepository.createUser(this)

    private fun ResultActions.thenGetRoomSuccessfully() {
        val room = roomRepository.findById(testRoom.roomId!!)!!
        room.let {
            andExpect(status().isOk)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(it.roomId!!.value))
                .andExpect(jsonPath("$.name").value(it.name))
                .andExpect(jsonPath("$.game.id").value(it.game.id!!.value))
                .andExpect(jsonPath("$.game.name").value(it.game.displayName))
                .andExpect(jsonPath("$.host.id").value(it.host.id.value))
                .andExpect(jsonPath("$.host.nickname").value(it.host.nickname))
                .andExpect(jsonPath("$.host.isReady").value(it.host.readiness))
                .andExpect(jsonPath("$.isLocked").value(!it.password.isNullOrEmpty()))
                .andExpect(jsonPath("$.status").value(it.status.toString()))
                .andExpect(jsonPath("$.currentPlayers").value(2))
                .andExpect(jsonPath("$.minPlayers").value(it.minPlayers))
                .andExpect(jsonPath("$.maxPlayers").value(it.maxPlayers))
                .andExpect(jsonPath("$.players").isArray())

            it.players.forEachIndexed { index, player ->
                andExpect(jsonPath("$.players[$index].id").value(player.id.value))
                    .andExpect(jsonPath("$.players[$index].nickname").value(player.nickname))
                    .andExpect(jsonPath("$.players[$index].isReady").value(player.readiness))
            }
        }
    }

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
            backEndUrl = "https://example.com/api/game01",
            createdOn = Instant.parse("2024-03-01T14:00:00.00Z"),
        )
    )

    private fun createRoom(host: User, password: String? = null, hostReady: Boolean = true): Room =
        roomRepository.createRoom(
            Room(
                game = testGame,
                host = Player(Player.Id(host.id!!.value), host.nickname),
                players = mutableListOf(Player(Player.Id(host.id!!.value), host.nickname, hostReady)),
                maxPlayers = testGame.maxPlayers,
                minPlayers = testGame.minPlayers,
                name = "Room-${randomUUID().toString().take(5)}",
                status = Room.Status.WAITING,
                password = password
            )
        )

    private fun createRoom(
        host: Player,
        players: MutableList<Player>,
        password: String? = null,
        status: Status? = WAITING,
    ): Room {
        return roomRepository.createRoom(
            Room(
                game = testGame,
                host = Player(Player.Id(host.id!!.value), host.nickname, true),
                players = players,
                maxPlayers = testGame.maxPlayers,
                minPlayers = testGame.minPlayers,
                name = "My Room",
                status = status!!,
                password = password
            )
        )
    }

    private fun createRoomRequest(password: String? = null): TestCreateRoomRequest =
        TestCreateRoomRequest(
            name = "Rapid Mahjong Room",
            gameId = testGame.id!!.value,
            password = password,
            maxPlayers = testGame.maxPlayers,
            minPlayers = testGame.minPlayers,
        )

    private fun joinRoomRequest(password: String? = null): TestJoinRoomRequest =
        TestJoinRoomRequest(
            password = password
        )

    private fun assertRoomPlayerGetReady(room: Room, user: User) {
        val player = findRoomById(room.roomId!!)!!.players.find { it.id.value == user.id!!.value }
        assertThat(player).isNotNull
        assertThat(player!!.readiness).isEqualTo(true)
    }

    private fun assertRoomPlayerNotReady(room: Room, user: User) {
        val player = findRoomById(room.roomId!!)!!.players.find { it.id.value == user.id!!.value }
        assertThat(player).isNotNull
        assertThat(player!!.readiness).isEqualTo(false)
    }

    private fun givenHostCreatedRoomAndPlayerJoined(host: User, player: User): Room {
        val room = givenTheHostCreatePublicRoom(host)
        room.whenUserJoinTheRoom(player)
        return room
    }

    private fun findRoomById(roomId: Room.Id): Room? =
        roomRepository.findById(roomId)

    private fun thenPlayersShouldBeInTheRoom(roomId: Room.Id, vararg users: User) =
        findRoomById(roomId)!!.players.map { it.id.value }.let {
            assertThat(it).containsAll(users.map { user -> user.id!!.value })
        }

    private fun User.toRoomPlayer(): Player =
        Player(Player.Id(id!!.value), nickname)

    private fun Room.isHost(playerId: Player.Id): Boolean =
        host.id == playerId

    private fun mockGameService(): OngoingStubbing<StartGameResponse> =
        `when`(gameService.startGame(anyString(), anyString(), anyObject()))

    private fun User.givenRoomHaveFourReadyPlayers(): Room {
        val userB = defaultUser("2").createUser()
        val userC = defaultUser("3").createUser()
        val userD = defaultUser("4").createUser()

        val room = givenTheHostCreatePublicRoom(this)
        userB.joinRoomAndReady(room)
        userC.joinRoomAndReady(room)
        userD.joinRoomAndReady(room)
        return roomRepository.findById(room.roomId!!)!!
    }

    private fun User.joinRoomAndReady(room: Room) {
        room.whenUserJoinTheRoom(this)
        whenUserGetReadyFor(room.roomId!!, this)
    }

    private fun User.whenHostStartGame(room: Room): ResultActions =
        mockMvc.perform(
            post("/rooms/${room.roomId!!.value}:startGame")
                .withJwt(toJwt())
                .withJson(StartGameRequest(room.roomId!!.value, room.players.map { it.toGamePlayer() }))
        )

    private fun Player.toGamePlayer(): StartGameRequest.GamePlayer =
        StartGameRequest.GamePlayer(id.value, nickname, nickname)

    private fun ResultActions.thenGameShouldBeStarted(room: Room) {
        andExpect(status().isOk)
            .andExpect(jsonPath("$.url").value("${room.game.frontEndUrl}/games/${room.game.id!!.value}"))
    }

    private fun ResultActions.thenConnectGameServerFailAndStartGameShouldBeFailed(message: String) {
        andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value(message))
    }

    private fun ResultActions.thenRoomAndPlayersStatusAreChanged() {
        andExpect(status().isNoContent)
        val room = roomRepository.findById(testRoom.roomId!!)!!
        room.let {
            assertEquals(WAITING, it.status)
            assertFalse(it.isEmpty())
            assertTrue(it.host.readiness)
            it.players.forEach { player ->
                if (!it.isHost(player.id)) {
                    assertFalse(player.readiness)
                }
            }
        }
    }
}
