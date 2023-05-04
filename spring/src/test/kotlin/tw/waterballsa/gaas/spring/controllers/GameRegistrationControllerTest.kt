package tw.waterballsa.gaas.spring.controllers

import org.assertj.core.api.Assertions.assertThat
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.spring.model.TestGameRegistrationRequest
import java.util.UUID.randomUUID

@SpringBootTest
@ActiveProfiles(profiles = ["dev"])
@AutoConfigureMockMvc(addFilters = false)
class GameRegistrationControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var gameRegistrationRepository: GameRegistrationRepository

    @MockBean
    lateinit var eventBus: EventBus

    @BeforeEach
    fun cleanUp() {
        gameRegistrationRepository.deleteAll()
    }

    @Test
    fun givenThereAreNoGames_WhenRegisteringANewGame_ThenItShouldBeSuccessfullyRegistered() {
        val request = TestGameRegistrationRequest(
            uniqueName = "big2-java",
            displayName = "大老二-Java",
            shortDescription = "A simple game.",
            rule = "Follow the rules to win.",
            imageUrl = "https://example.com/game01.jpg",
            minPlayers = 2,
            maxPlayers = 4,
            frontEndUrl = "https://example.com/play/game01",
            backEndUrl = "https://example.com/api/game01"
        )

        registerGameSuccessfully(request)
    }

    @Test
    fun givenUnoIsAlreadyRegistered_WhenRegisteringUnoAgain_ThenRejectTheRegistration() {
        val request = TestGameRegistrationRequest(
            uniqueName = "uno-java",
            displayName = "UNO-Java",
            shortDescription = "經典的友情破壞遊戲",
            rule = "牌多的輸，牌少的贏",
            imageUrl = "https://example.com/game01.jpg",
            minPlayers = 2,
            maxPlayers = 4,
            frontEndUrl = "https://example.com/play/game01",
            backEndUrl = "https://example.com/api/game01"
        )

        registerGameSuccessfully(request)

        registerGame(request)
            .andExpect(status().isBadRequest)
            .andExpect(content().string("${request.uniqueName} already exists"))
    }

    @Test
    fun givenTheRandomGameHasBeenRegistered_WhenRegisteringMahjong_ThenTheGameListShouldShowTwoGames() {
        registerRandomGame()
        val request = TestGameRegistrationRequest(
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

        registerGameSuccessfully(request)

        val numberOfTotalGameRegistrations = gameRegistrationRepository.getNumberOfTotalGameRegistrations()
        assertThat(numberOfTotalGameRegistrations).isEqualTo(2)
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

    private fun registerGameSuccessfully(testGameRegistrationRequest: TestGameRegistrationRequest): ResultActions =
        with(testGameRegistrationRequest) {
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
}
