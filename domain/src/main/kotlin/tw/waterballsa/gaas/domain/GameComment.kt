package tw.waterballsa.gaas.domain

import java.time.Instant

class GameComment(
    val id: Id? = null,
    val gameId: GameRegistration.Id,
    val userId: User.Id,
    var rating: Int,
    var comment: String,
    var lastUpdatedTime: Instant,
    val createdTime: Instant,
) {
    constructor(gameId: GameRegistration.Id, userId: User.Id, rating: Int, comment: String) :
        this(null, gameId, userId, rating, comment, Instant.now(), Instant.now())

    @JvmInline
    value class Id(val value: String)
}
