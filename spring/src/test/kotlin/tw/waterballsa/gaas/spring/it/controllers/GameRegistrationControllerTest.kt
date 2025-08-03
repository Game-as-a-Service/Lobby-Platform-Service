package tw.waterballsa.gaas.spring.it.controllers

import com.fasterxml.jackson.core.type.TypeReference
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.events.CommentGameEvent
import tw.waterballsa.gaas.events.StartedGameEvent
import tw.waterballsa.gaas.events.enums.EventMessageType
import tw.waterballsa.gaas.spring.controllers.RegisterGamePresenter.RegisterGameViewModel
import tw.waterballsa.gaas.spring.controllers.viewmodel.GameRegistrationViewModel
import tw.waterballsa.gaas.spring.controllers.viewmodel.UpdateGameRegistrationViewModel
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest
import tw.waterballsa.gaas.spring.models.TestGameRegistrationRequest
import java.util.UUID.randomUUID

@AutoConfigureMockMvc(addFilters = false)
class GameRegistrationControllerTest @Autowired constructor(
    val gameRegistrationRepository: GameRegistrationRepository,
    val eventBus: EventBus,
) : AbstractSpringBootTest() {

    @BeforeEach
    fun cleanUp() {
        gameRegistrationRepository.deleteAll()
    }

    @Test
    fun whenRegisteringANewGame_ThenItShouldBeSuccessfullyRegistered() {
        val request = createGameRegistrationRequest("big2-Java", "Big2 Java")

        registerGameSuccessfully(request)
            .also { big2ViewModel ->
                val gameRegistration = findGameRegistration(big2ViewModel)
                big2ViewModel.validateWithGameRegistration(gameRegistration)
            }
    }

    @Test
    fun givenUnoIsAlreadyRegistered_WhenRegisteringUnoAgain_ThenRejectTheRegistration() {
        val request = createGameRegistrationRequest("uno-java", "UNO Java")
        val unoViewModel = registerGameSuccessfully(request)

        registerGame(request)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("${request.uniqueName} already exists"))

        unoViewModel.let {
            val gameRegistration = findGameRegistration(it)
            it.validateWithGameRegistration(gameRegistration)
        }
    }

    @Test
    fun givenTheRandomGameHasBeenRegistered_WhenRegisteringMahjong_ThenTheGameListShouldShowTwoGames() {
        registerRandomGame()
        val request = createGameRegistrationRequest("mahjong-python", "Mahjong Python")
        val mahjongViewModel = registerGameSuccessfully(request)

        val numberOfTotalGameRegistrations = gameRegistrationRepository.getNumberOfTotalGameRegistrations()
        assertThat(numberOfTotalGameRegistrations).isEqualTo(2)
        mahjongViewModel.let {
            val gameRegistration = findGameRegistration(it)
            it.validateWithGameRegistration(gameRegistration)
        }
    }

    @Test
    fun givenUnoAndBig2HasBeenRegistered_WhenTheUserViewsGameList_ThenGameListShouldContainTheseTwoGames() {
        val big2Request = createGameRegistrationRequest("big2-Java", "Big2 Java")
        val unoRequest = createGameRegistrationRequest("uno-java", "UNO Java")
        registerGameSuccessfully(big2Request)
        registerGameSuccessfully(unoRequest)

        val getGamesViewModels = mockMvc.perform(get("/games"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.size()").value(2))
            .validateSpecificElement(0, big2Request)
            .validateSpecificElement(1, unoRequest)
            .getBody(object : TypeReference<List<GameRegistrationViewModel>>() {})

        val gameRegistrations = gameRegistrationRepository.findGameRegistrations()
        getGamesViewModels.forEachIndexed { i, model -> model.validateWithGameRegistration(gameRegistrations[i]) }
    }

    @Test
    fun givenBig2IsNewerThanUno_WhenThenUserViewsGameListByCreateOn_ThenBig2RankInFront() {
        val unoRequest = createGameRegistrationRequest("uno-java", "UNO Java")
        val big2Request = createGameRegistrationRequest("big2-Java", "Big2 Java")
        registerGameSuccessfully(unoRequest)
        registerGameSuccessfully(big2Request)

        mockMvc.perform(get("/games?sort_by=createdOn"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.size()").value(2))
            .validateSpecificElement(0, big2Request)
            .validateSpecificElement(1, unoRequest)
            .getBody(object : TypeReference<List<GameRegistrationViewModel>>() {})
    }

    @Test
    fun givenBig2TimesPlayedIsGreaterThanUnoTimesPlayed_WhenThenUserViewsGameListByTimesPlayed_ThenBig2RankInFront() {
        val unoRequest = createGameRegistrationRequest("uno-java", "UNO Java")
        val big2Request = createGameRegistrationRequest("big2-Java", "Big2 Java")
        registerGameSuccessfully(unoRequest)
        val big2 = registerGameSuccessfully(big2Request)
        eventBus.broadcast(
            StartedGameEvent(
                EventMessageType.GAME_STARTED,
                StartedGameEvent.Data("", Room.Id(""), big2.id, emptyList())
            )
        )
        mockMvc.perform(get("/games?sort_by=timesPlayed"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.size()").value(2))
            .validateSpecificElement(0, big2Request)
            .validateSpecificElement(1, unoRequest)
            .getBody(object : TypeReference<List<GameRegistrationViewModel>>() {})
    }

    @Test
    fun givenBig2RatingIsGreaterThanUnoRating_WhenThenUserViewsGameListByRating_ThenBig2RankInFront() {
        val unoRequest = createGameRegistrationRequest("uno-java", "UNO Java")
        val big2Request = createGameRegistrationRequest("big2-Java", "Big2 Java")
        registerGameSuccessfully(unoRequest)
        val big2 = registerGameSuccessfully(big2Request)

        eventBus.broadcast(CommentGameEvent(big2.id, User.Id(""), 5, 1))

        mockMvc.perform(get("/games?sort_by=rating"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.size()").value(2))
            .validateSpecificElement(0, big2Request)
            .validateSpecificElement(1, unoRequest)
            .getBody(object : TypeReference<List<GameRegistrationViewModel>>() {})
    }


    @Test
    fun givenBig2HasRegistered_whenUpdateGameRegistrationWithWrongId_thenShouldReturnGameRegistrationNotFound() {
        givenGameHasRegistered("big2", "Big2")
        whenUpdateGameRegistration(
            "not-exist-game-id",
            TestGameRegistrationRequest(
                "big2",
                "updated big2",
                "updated big2 description",
                "updated big2 rules",
                "updated big2 image url",
                3,
                8,
                "updated big2 frontend url",
                "updated big2 backend url",
            )
        )
            .thenShouldReturnGameRegistrationNotFound()
    }

    @Test
    fun givenBig2AndUnoHasRegistered_whenUpdateBig2UniqueNameAsUno_thenShouldReturnGameAlreadyExists() {
        val big2GameId = givenGameHasRegistered("big2", "Big2")
        val unoGameId = givenGameHasRegistered("uno", "UNO")

        whenUpdateGameRegistration(
            big2GameId.value,
            TestGameRegistrationRequest(
                "uno",
                "updated big2",
                "updated big2 description",
                "updated big2 rules",
                "updated big2 image url",
                3,
                8,
                "updated big2 frontend url",
                "updated big2 backend url",
            )
        )
            .thenShouldReturnGameAlreadyExists()
    }

    @Test
    fun givenBig2HasRegistered_whenUpdateGameRegistrationWithRightIdAndUniqueName_thenUpdateGameRegistrationSuccessfully() {
        val big2GameId = givenGameHasRegistered("big2", "Big2")
        val updateGameRegistrationRequest = TestGameRegistrationRequest(
            "big2",
            "updated big2",
            "updated big2 description",
            "updated big2 rules",
            "updated big2 image url",
            3,
            8,
            "updated big2 frontend url",
            "updated big2 backend url",
        )

        whenUpdateGameRegistration(big2GameId.value, updateGameRegistrationRequest)
            .thenUpdateGameRegistrationSuccessfully(updateGameRegistrationRequest)
    }

    private fun createGameRegistrationRequest(
        uniqueName: String,
        displayName: String,
        minPlayers: Int = 2,
        maxPlayers: Int = 4
    ) = TestGameRegistrationRequest(
        uniqueName = uniqueName,
        displayName = displayName,
        shortDescription = "A simple game.",
        rule = "Follow the rules to win.",
        imageUrl = "https://example.com/game01.jpg",
        minPlayers = minPlayers,
        maxPlayers = maxPlayers,
        frontEndUrl = "https://example.com/play/game01",
        backEndUrl = "https://example.com/api/game01"
    )

    private fun registerGameSuccessfully(testGameRegistrationRequest: TestGameRegistrationRequest): RegisterGameViewModel {
        return with(testGameRegistrationRequest) {
            registerGame(this)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.uniqueName").value(uniqueName))
                .andExpect(jsonPath("$.displayName").value(displayName))
                .andExpect(jsonPath("$.shortDescription").value(shortDescription))
                .andExpect(jsonPath("$.rule").value(rule))
                .andExpect(jsonPath("$.imageUrl").value(imageUrl))
                .andExpect(jsonPath("$.minPlayers").value(minPlayers))
                .andExpect(jsonPath("$.maxPlayers").value(maxPlayers))
                .andExpect(jsonPath("$.frontEndUrl").value(frontEndUrl))
                .andExpect(jsonPath("$.backEndUrl").value(backEndUrl))
                .getBody(object : TypeReference<RegisterGameViewModel>() {})
        }
    }

    private fun findGameRegistration(viewModel: RegisterGameViewModel): GameRegistration =
        viewModel.let { gameRegistrationRepository.findById(it.id) ?: fail("GameRegistration not found") }

    private fun RegisterGameViewModel.validateWithGameRegistration(gameRegistration: GameRegistration) {
        gameRegistration.let {
            assertThat(it.id).isEqualTo(id)
            assertThat(it.uniqueName).isEqualTo(uniqueName)
            assertThat(it.displayName).isEqualTo(displayName)
            assertThat(it.shortDescription).isEqualTo(shortDescription)
            assertThat(it.rule).isEqualTo(rule)
            assertThat(it.imageUrl).isEqualTo(imageUrl)
            assertThat(it.minPlayers).isEqualTo(minPlayers)
            assertThat(it.maxPlayers).isEqualTo(maxPlayers)
            assertThat(it.frontEndUrl).isEqualTo(frontEndUrl)
            assertThat(it.backEndUrl).isEqualTo(backEndUrl)
            assertThat(it.createdOn).isEqualTo(createdOn)
        }
    }

    private fun registerGame(testGameRegistrationRequest: TestGameRegistrationRequest): ResultActions =
        with(testGameRegistrationRequest) {
            mockMvc.perform(
                post("/games")
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                            {
                                "uniqueName": "$uniqueName",
                                "displayName": "$displayName",
                                "shortDescription": "$shortDescription",
                                "rule": "$rule",
                                "imageUrl": "$imageUrl",
                                "minPlayers": $minPlayers,
                                "maxPlayers": $maxPlayers,
                                "frontEndUrl": "$frontEndUrl",
                                "backEndUrl": "$backEndUrl"
                            }
                        """.trimIndent()
                    )
            )
        }

    private fun registerRandomGame() {
        registerGameSuccessfully(
            TestGameRegistrationRequest(
                uniqueName = randomUUID().toString(),
                displayName = "Dummy Game",
                shortDescription = "A random game.",
                rule = "Follow the rules to win.",
                imageUrl = "https://example.com/game01.jpg",
                minPlayers = 2,
                maxPlayers = 4,
                frontEndUrl = "https://example.com/play/game01",
                backEndUrl = "https://example.com/api/game01"
            )
        )
    }

    private fun ResultActions.validateSpecificElement(
        index: Int,
        testGameRegistrationRequest: TestGameRegistrationRequest
    ): ResultActions =
        with(testGameRegistrationRequest) {
            this@validateSpecificElement.andExpect(jsonPath("$[$index].id").exists())
                .andExpect(jsonPath("$[$index].name").value(displayName))
                .andExpect(jsonPath("$[$index].img").value(imageUrl))
                .andExpect(jsonPath("$[$index].minPlayers").value(minPlayers))
                .andExpect(jsonPath("$[$index].maxPlayers").value(maxPlayers))
        }

    private fun GameRegistrationViewModel.validateWithGameRegistration(gameRegistration: GameRegistration) {
        gameRegistration.let {
            assertThat(it.id).isEqualTo(id)
            assertThat(it.displayName).isEqualTo(name)
            assertThat(it.imageUrl).isEqualTo(img)
            assertThat(it.minPlayers).isEqualTo(minPlayers)
            assertThat(it.maxPlayers).isEqualTo(maxPlayers)
            assertThat(it.createdOn).isEqualTo(createdOn)
        }
    }

    private fun givenGameHasRegistered(uniqueName: String, displayName: String): GameRegistration.Id {
        val createGameRegistrationRequest = createGameRegistrationRequest(uniqueName, displayName)
        return registerGameSuccessfully(createGameRegistrationRequest).id
    }

    private fun whenUpdateGameRegistration(
        gameId: String,
        updateGameRegistrationRequest: TestGameRegistrationRequest
    ): ResultActions {
        return mockMvc.perform(
            put("/games/$gameId")
                .withJson(updateGameRegistrationRequest)
        )
    }

    private fun ResultActions.thenShouldReturnGameRegistrationNotFound() {
        andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Resource (GameRegistration) not found (id = Id(value=not-exist-game-id))."))
    }

    private fun ResultActions.thenShouldReturnGameAlreadyExists() {
        andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("uno already exists"))
    }

    private fun ResultActions.thenUpdateGameRegistrationSuccessfully(request: TestGameRegistrationRequest) {
        val view = andExpect(status().isOk)
            .getBody(UpdateGameRegistrationViewModel::class.java)

        gameRegistrationRepository.findById(view.id)
            .also {
                assertThat(it).isNotNull
                assertThat(it!!.uniqueName).isEqualTo(request.uniqueName)
                assertThat(it.displayName).isEqualTo(request.displayName)
                assertThat(it.shortDescription).isEqualTo(request.shortDescription)
                assertThat(it.rule).isEqualTo(request.rule)
                assertThat(it.imageUrl).isEqualTo(request.imageUrl)
                assertThat(it.minPlayers).isEqualTo(request.minPlayers)
                assertThat(it.maxPlayers).isEqualTo(request.maxPlayers)
                assertThat(it.frontEndUrl).isEqualTo(request.frontEndUrl)
                assertThat(it.backEndUrl).isEqualTo(request.backEndUrl)
            }
    }

}
