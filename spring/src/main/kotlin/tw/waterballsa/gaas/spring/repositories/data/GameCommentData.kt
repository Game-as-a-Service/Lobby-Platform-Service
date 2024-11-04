package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import tw.waterballsa.gaas.domain.GameComment
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User
import java.time.Instant

@Document
class GameCommentData(
    @Id
    var id: String?,
    var gameId: String,
    var userId: String,
    var rating: Int,
    var comment: String,
    var createdTime: Instant,
    var lastUpdatedTime: Instant,
) {
    fun toDomain(): GameComment = GameComment(
        id?.let { GameComment.Id(it) }, GameRegistration.Id(gameId), User.Id(userId), rating, comment,
        lastUpdatedTime, createdTime,
    )
}

fun GameComment.toData(): GameCommentData = GameCommentData(
    id?.value, gameId.value, userId.value, rating, comment, createdTime, lastUpdatedTime
)