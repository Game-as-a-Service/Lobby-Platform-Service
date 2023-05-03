package tw.waterballsa.gaas.spring.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.domain.GameRegistration
import java.util.UUID.randomUUID

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(profiles = ["dev"])
class GameRegistrationControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var gameRegistrationRepository: GameRegistrationRepository

    @BeforeEach
    fun cleanUp() {
        gameRegistrationRepository.deleteAll()
    }

    @Test
    fun registerGame() {
        mockMvc.perform(
            post("/games")
                .contentType(APPLICATION_JSON)
                .content(
                    """
                        {
                            "uniqueName": "big2-java",
                            "displayName": "大老二-Java",
                            "shortDescription": "A simple game.",
                            "rule": "Follow the rules to win.",
                            "imageUrl": "https://example.com/game01.jpg",
                            "minPlayers": 2,
                            "maxPlayers": 4,
                            "frontEndUrl": "https://example.com/play/game01",
                            "backEndUrl": "https://example.com/api/game01"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.uniqueName").value("big2-java"))
            .andExpect(jsonPath("$.displayName").value("大老二-Java"))
            .andExpect(jsonPath("$.shortDescription").value("A simple game."))
            .andExpect(jsonPath("$.rule").value("Follow the rules to win."))
            .andExpect(jsonPath("$.imageUrl").value("https://example.com/game01.jpg"))
            .andExpect(jsonPath("$.minPlayers").value(2))
            .andExpect(jsonPath("$.maxPlayers").value(4))
            .andExpect(jsonPath("$.frontEndUrl").value("https://example.com/play/game01"))
            .andExpect(jsonPath("$.backEndUrl").value("https://example.com/api/game01"))
    }

    @Test
    fun registerRedundantGame() {
        gameRegistrationRepository.registerGame(GameRegistration(
            uniqueName = "uno-java",
            displayName = "UNO-Java",
            shortDescription = "經典的友情破壞遊戲",
            rule = "牌多的輸，牌少的贏",
            imageUrl = "https://example.com/game01.jpg",
            minPlayers = 2,
            maxPlayers = 4,
            frontEndUrl = "https://example.com/play/game01",
            backEndUrl = "https://example.com/api/game01"
        ))

        mockMvc.perform(
            post("/games")
                .contentType(APPLICATION_JSON)
                .content(
                    """
                        {
                            "uniqueName": "uno-java",
                            "displayName": "UNO-Java 改",
                            "shortDescription": "A simple game.",
                            "rule": "Follow the rules to win.",
                            "imageUrl": "https://example.com/game01.jpg",
                            "minPlayers": 2,
                            "maxPlayers": 4,
                            "frontEndUrl": "https://example.com/play/game01",
                            "backEndUrl": "https://example.com/api/game01"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun registerDifferentGame() {
        registerRandomGame()
        val originalNumbersOfGameRegistration = gameRegistrationRepository.getNumberOfTotalGameRegistrations()

        mockMvc.perform(
            post("/games")
                .contentType(APPLICATION_JSON)
                .content(
                    """
                        {
                            "uniqueName": "Mahjong-python",
                            "displayName": "麻將-Python",
                            "shortDescription": "A simple game.",
                            "rule": "Follow the rules to win.",
                            "imageUrl": "https://example.com/game01.jpg",
                            "minPlayers": 2,
                            "maxPlayers": 4,
                            "frontEndUrl": "https://example.com/play/game01",
                            "backEndUrl": "https://example.com/api/game01"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.uniqueName").value("Mahjong-python"))
            .andExpect(jsonPath("$.displayName").value("麻將-Python"))
            .andExpect(jsonPath("$.shortDescription").value("A simple game."))
            .andExpect(jsonPath("$.rule").value("Follow the rules to win."))
            .andExpect(jsonPath("$.imageUrl").value("https://example.com/game01.jpg"))
            .andExpect(jsonPath("$.minPlayers").value(2))
            .andExpect(jsonPath("$.maxPlayers").value(4))
            .andExpect(jsonPath("$.frontEndUrl").value("https://example.com/play/game01"))
            .andExpect(jsonPath("$.backEndUrl").value("https://example.com/api/game01"))

        assertThat(gameRegistrationRepository.getNumberOfTotalGameRegistrations()).isEqualTo(originalNumbersOfGameRegistration + 1)
    }

    private fun registerRandomGame() {
        gameRegistrationRepository.registerGame(
            GameRegistration(
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
}
