package tw.waterballsa.gaas.domain

import java.time.Instant

class GameRegistration(
    val id: Id? = null,
    var uniqueName: String,
    var displayName: String,
    var shortDescription: String,
    var rule: String,
    var imageUrl: String,
    var minPlayers: Int,
    var maxPlayers: Int,
    var frontEndUrl: String,
    var backEndUrl: String,
    val createdOn: Instant,
) {
    @JvmInline
    value class Id(val value: String)
}
