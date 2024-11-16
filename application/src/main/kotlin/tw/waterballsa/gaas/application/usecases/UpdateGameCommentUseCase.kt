package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.GameCommentRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameComment
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.events.CommentGameEvent
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.enums.PlatformError
import java.time.Instant
import javax.inject.Named

@Named
class UpdateGameCommentUseCase(
    private val userRepository: UserRepository,
    private val gameRatingRepository: GameCommentRepository,
    private val eventBus: EventBus,
) {

    fun execute(request: Request) {
        val commentUser = getCommentUser(request.identityProviderId)
        val gameId = GameRegistration.Id(request.gameId)
        val userId = commentUser.id!!

        val gameComment = gameRatingRepository.findByGameIdAndUserId(gameId, userId)
            ?: throw notFound(PlatformError.GAME_COMMENT_NOT_FOUND, GameComment::class).message()
        val originRating = gameComment.rating

        gameComment.apply {
            rating = request.rating
            comment = request.comment
            lastUpdatedTime = Instant.now()
        }

        gameRatingRepository.updateGameComment(gameComment)
        eventBus.broadcast(CommentGameEvent(gameId, userId, originRating - request.rating.toLong(), 0))
    }

    private fun getCommentUser(identityProviderId: String): User {
        return userRepository.findByIdentity(identityProviderId)
            ?: throw notFound(PlatformError.USER_NOT_FOUND, User::class).message()
    }

    data class Request(
        val identityProviderId: String,
        val gameId: String,
        val rating: Int,
        val comment: String,
    )
}
