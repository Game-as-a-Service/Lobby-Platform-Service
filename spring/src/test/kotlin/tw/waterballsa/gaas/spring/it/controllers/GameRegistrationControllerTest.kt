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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.spring.controllers.RegisterGamePresenter.RegisterGameViewModel
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest
import tw.waterballsa.gaas.spring.models.TestGameRegistrationRequest
import java.util.UUID.randomUUID

@AutoConfigureMockMvc(addFilters = false)
class GameRegistrationControllerTest @Autowired constructor(
    val gameRegistrationRepository: GameRegistrationRepository,
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
            .andExpect(content().string("${request.uniqueName} already exists"))

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

        val registerGameViewModels = mockMvc.perform(get("/games"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.size()").value(2))
            .validateSpecificElement(0, big2Request)
            .validateSpecificElement(1, unoRequest)
            .getBody(object : TypeReference<List<RegisterGameViewModel>>() {})

        val gameRegistrations = gameRegistrationRepository.findGameRegistrations()
        registerGameViewModels.forEachIndexed { i, model -> model.validateWithGameRegistration(gameRegistrations[i]) }
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
                .getBody(RegisterGameViewModel::class.java)
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
                .andExpect(jsonPath("$[$index].uniqueName").value(uniqueName))
                .andExpect(jsonPath("$[$index].displayName").value(displayName))
                .andExpect(jsonPath("$[$index].shortDescription").value(shortDescription))
                .andExpect(jsonPath("$[$index].rule").value(rule))
                .andExpect(jsonPath("$[$index].imageUrl").value(imageUrl))
                .andExpect(jsonPath("$[$index].minPlayers").value(minPlayers))
                .andExpect(jsonPath("$[$index].maxPlayers").value(maxPlayers))
                .andExpect(jsonPath("$[$index].frontEndUrl").value(frontEndUrl))
                .andExpect(jsonPath("$[$index].backEndUrl").value(backEndUrl))
        }

}
