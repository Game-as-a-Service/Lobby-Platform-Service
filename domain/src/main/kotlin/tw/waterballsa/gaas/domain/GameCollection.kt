package tw.waterballsa.gaas.domain

import java.time.Instant

class GameCollection(
    val id: Id?,
    val gameId: GameRegistration.Id,
    val userId: User.Id,
    val collectTime: Instant,
) {
    constructor(gameId: GameRegistration.Id, userId: User.Id) : this(null, gameId, userId, Instant.now())

    @JvmInline
    value class Id(val value: String)
}
