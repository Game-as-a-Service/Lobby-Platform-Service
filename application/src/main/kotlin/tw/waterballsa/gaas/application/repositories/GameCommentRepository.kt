package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.GameComment
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User

interface GameCommentRepository {
    fun commentGame(gameComment: GameComment)
    fun updateGameComment(gameComment: GameComment)
    fun findByGameIdAndUserId(gameId: GameRegistration.Id, userId: User.Id): GameComment?
}
