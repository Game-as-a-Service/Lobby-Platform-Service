package tw.waterballsa.gaas.spring.it.controllers

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest
import tw.waterballsa.gaas.spring.models.TestCommentGameRequest
import tw.waterballsa.gaas.spring.models.TestUpdateGameCommentRequest
import tw.waterballsa.gaas.spring.utils.Users.Companion.defaultUserBuilder
import java.time.Instant
import java.util.UUID.randomUUID

class GameCommentControllerTest @Autowired constructor(
    val userRepository: UserRepository,
    val gameRegistrationRepository: GameRegistrationRepository,
) : AbstractSpringBootTest() {
    lateinit var testGame: GameRegistration

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
        gameRegistrationRepository.deleteAll()

        testGame = registerGame()
    }

    @Test
    fun givenUserHasPlayedMahjongGame_whenUserCommentGame_thenShouldSuccessful() {
        val gameId = testGame.id!!
        val user = prepareUser(listOf(gameId))
        user.whenCommentGame(gameId.value)
            .thenActionSuccessfully()
    }


    @Test
    fun givenUserHasNotPlayedMahjongGame_whenUserCommentGame_thenShouldFail(){
        val gameId = testGame.id!!
        val user = prepareUser(emptyList())
        user.whenCommentGame(gameId.value)
            .thenShouldFail("Must play game before comment.")
    }

    @Test
    fun givenUserAlreadyCommentMahjongGame_whenUserComment_thenShouldFail(){
        val gameId = testGame.id!!
        val user = prepareUser(listOf(gameId))
        user.whenCommentGame(gameId.value)
        user.whenCommentGame(gameId.value)
            .thenShouldFail("Game already commented.")
    }

    @Test
    fun givenUserAlreadyCommentMahjongGame_whenUserUpdateComment_thenShouldSuccessful(){
        val gameId = testGame.id!!
        val user = prepareUser(listOf(gameId))
        user.whenCommentGame(gameId.value)
        user.whenUpdateComment(gameId.value)
            .thenActionSuccessfully()
    }

    @Test
    fun givenUserNotCommentMahjongGame_whenUserUpdateComment_thenShouldNotFound(){
        val gameId = testGame.id!!
        val user = prepareUser(listOf(gameId))
        user.whenUpdateComment(gameId.value)
            .thenShouldNotFound("GameComment not found")
    }

    private fun prepareUser(playedGamesIds: List<GameRegistration.Id>): User {
        val user = defaultUserBuilder("1")
            .nickname("user-${randomUUID()}")
            .identities("google-oauth2|102527320242660434908")
            .lastPlayedGameId(playedGamesIds.lastOrNull())
            .playedGamesIds(playedGamesIds.toSet())
            .build()
        userRepository.createUser(user)
        return user
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

    private fun User.whenCommentGame(
        gameId: String,
        rating: Int = 5,
        comment: String = "It's goooood!"
    ): ResultActions {
        return mockMvc.perform(
            post("/comments")
                .withJwt(toJwt())
                .withJson(TestCommentGameRequest(gameId, rating, comment))
        )
    }

    private fun User.whenUpdateComment(
        gameId: String,
        rating: Int = 3,
        comment: String = "Normal!"
    ): ResultActions {
        return mockMvc.perform(
            post("/comments/games/${gameId}")
                .withJwt(toJwt())
                .withJson(TestUpdateGameCommentRequest(rating, comment))
        )
    }

    private fun ResultActions.thenActionSuccessfully(): ResultActions {
        return andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("success"))
    }

    private fun ResultActions.thenShouldFail(message: String): ResultActions {
        return andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value(message))
    }

    private fun ResultActions.thenShouldNotFound(message: String): ResultActions{
        return andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value(message))
    }
}