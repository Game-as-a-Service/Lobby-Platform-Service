package tw.waterballsa.gaas.spring.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.repositories.GameCommentRepository
import tw.waterballsa.gaas.domain.GameComment
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.repositories.dao.GameCommentDAO
import tw.waterballsa.gaas.spring.repositories.data.toData

@Component
class SpringGameCommentRepository(
    private val gameCommentDAO: GameCommentDAO,
): GameCommentRepository {
    override fun commentGame(gameComment: GameComment) {
        gameCommentDAO.save(gameComment.toData()).toDomain()
    }

    override fun updateGameComment(gameComment: GameComment) {
        gameCommentDAO.save(gameComment.toData()).toDomain()
    }

    override fun findByGameIdAndUserId(gameId: GameRegistration.Id, userId: User.Id): GameComment? {
        return gameCommentDAO.findByGameIdAndUserId(gameId.value, userId.value)?.toDomain()
    }
}