package tw.waterballsa.gaas.spring.it.controllers

import com.fasterxml.jackson.core.type.TypeReference
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.repositories.GameCollectionRepository
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameCollection
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.controllers.viewmodel.GameRegistrationViewModel
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest
import tw.waterballsa.gaas.spring.utils.Users.Companion.defaultUserBuilder
import java.time.Instant
import java.util.UUID.randomUUID

class GameCollectionControllerTest @Autowired constructor(
    val userRepository: UserRepository,
    val gameRegistrationRepository: GameRegistrationRepository,
    val gameCollectionRepository: GameCollectionRepository,
) : AbstractSpringBootTest() {
    lateinit var testGame: GameRegistration
    lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
        gameRegistrationRepository.deleteAll()

        testGame = registerGame()
        testUser = prepareUser()
    }

    @Test
    fun givenGameExist_whenUserCollectGame_thenShouldSuccessful() {
        testUser.whenCollectGame(testGame.id!!)
            .thenShouldSuccessful()
    }

    @Test
    fun givenGameNotExist_whenUserCollectGame_thenGameNotFound() {
        testUser.whenCollectGame(GameRegistration.Id("123"))
            .thenGameNotFound()
    }

    @Test
    fun givenUserCollectGame_whenUserUnCollectGame_thenShouldSuccessful() {
        gameCollectionRepository.collectGame(
            GameCollection(null, testGame.id!!, testUser.id!!, Instant.now())
        )
        testUser.whenUnCollectGame(testGame.id!!)
            .thenShouldSuccessful()
    }

    @Test
    fun givenUserCollectMahjongGameAndBig2_whenGetGameCollections_thenGameListShouldContainTheseTwoGames() {
        val big2 = registerBig2()
        testUser.whenCollectGame(testGame.id!!)
        testUser.whenCollectGame(big2.id!!)

        val gameCollections = testUser.whenGetGameCollections()
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.size()").value(2))
            .getBody(object : TypeReference<List<GameRegistrationViewModel>>() {})
        assertEquals(gameCollections[0].id, big2.id)
        assertEquals(gameCollections[1].id, testGame.id)
    }

    private fun User.whenCollectGame(gameId: GameRegistration.Id): ResultActions {
        return mockMvc.perform(
            post("/collections/games/${gameId.value}")
                .withJwt(toJwt())
        )
    }

    private fun User.whenUnCollectGame(gameId: GameRegistration.Id): ResultActions {
        return mockMvc.perform(
            delete("/collections/games/${gameId.value}")
                .withJwt(toJwt())
        )
    }

    private fun User.whenGetGameCollections(): ResultActions{
        return mockMvc.perform(
            get("/collections")
                .withJwt(toJwt())
        )
    }

    private fun ResultActions.thenShouldSuccessful(): ResultActions {
        return andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("success"))
    }

    private fun ResultActions.thenGameNotFound(): ResultActions {
        return andExpect(status().isNotFound)
            .andExpect(jsonPath("$.errorCode").value("G001"))
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

    private fun registerBig2(): GameRegistration = gameRegistrationRepository.registerGame(
        GameRegistration(
            uniqueName = "big2",
            displayName = "big2",
            shortDescription = "big2",
            rule = "big2",
            imageUrl = "https://example.com/game01.jpg",
            minPlayers = 3,
            maxPlayers = 8,
            frontEndUrl = "https://example.com/play/game01",
            backEndUrl = "https://example.com/api/game01",
            createdOn = Instant.parse("2024-03-01T14:00:00.00Z"),
        )
    )

    private fun prepareUser(): User {
        val user = defaultUserBuilder("1")
            .nickname("user-${randomUUID()}")
            .identities("google-oauth2|102527320242660434908")
            .lastPlayedGameId(null)
            .playedGamesIds(emptySet())
            .build()
        userRepository.createUser(user)
        return user
    }
}