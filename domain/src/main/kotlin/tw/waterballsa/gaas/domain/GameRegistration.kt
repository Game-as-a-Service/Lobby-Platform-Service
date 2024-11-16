package tw.waterballsa.gaas.domain

import java.math.BigDecimal
import java.math.RoundingMode
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
    val totalRating: Long? = null,
    val numberOfComments: Long? = null,
) {
    @JvmInline
    value class Id(val value: String)

    fun rating(): Double {
        val total = totalRating ?: 0
        val number = numberOfComments ?: 0
        return if (number == 0L) {
            0.0
        } else {
            BigDecimal.valueOf(total)
                .divide(BigDecimal.valueOf(number), 1, RoundingMode.HALF_UP)
                .toDouble()
        }
    }
}
