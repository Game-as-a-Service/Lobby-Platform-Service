package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.GameCommentRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameComment
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.events.CommentGameEvent
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError
import javax.inject.Named

@Named
class CommentGameUseCase(
    private val userRepository: UserRepository,
    private val gameRatingRepository: GameCommentRepository,
    private val eventBus: EventBus,
) {

    fun execute(request: Request) {
        val commentUser = getCommentUser(request.identityProviderId)
        val gameId = GameRegistration.Id(request.gameId)
        val userId = commentUser.id!!

        validateCommentEligibility(commentUser, gameId)
        createGameComment(gameId, userId, request.rating, request.comment)
        eventBus.broadcast(CommentGameEvent(gameId, userId, request.rating.toLong(), 1))
    }

    private fun getCommentUser(identityProviderId: String): User {
        return userRepository.findByIdentity(identityProviderId)
            ?: throw notFound(PlatformError.USER_NOT_FOUND, User::class).message()
    }

    private fun validateCommentEligibility(user: User, gameId: GameRegistration.Id) {
        user.validateGamePlayed(gameId)
        validateNoExistingRating(gameId, user.id!!)
    }

    private fun createGameComment(gameId: GameRegistration.Id, userId: User.Id, rating: Int, comment: String) {
        val newRating = GameComment(gameId, userId, rating, comment)
        gameRatingRepository.commentGame(newRating)
    }

    private fun User.validateGamePlayed(gameId: GameRegistration.Id) {
        val playedGamesIds = playedGamesIds ?: emptySet()
        if (gameId !in playedGamesIds) {
            throw PlatformException(PlatformError.GAME_NOT_PLAYED, "Must play game before comment.")
        }
    }

    private fun validateNoExistingRating(gameId: GameRegistration.Id, userId: User.Id) {
        if (gameRatingRepository.findByGameIdAndUserId(gameId, userId) != null) {
            throw PlatformException(PlatformError.GAME_COMMENT_DUPLICATED, "Game already commented.")
        }
    }

    data class Request(
        val identityProviderId: String,
        val gameId: String,
        val rating: Int,
        val comment: String,
    )
}
