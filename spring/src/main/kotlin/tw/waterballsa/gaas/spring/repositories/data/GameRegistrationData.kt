package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import tw.waterballsa.gaas.domain.GameRegistration
import java.time.Instant

@Document
class GameRegistrationData(
    @Id
    var id: String? = null,
    @Indexed(unique = true)
    var uniqueName: String? = null,
    var displayName: String? = null,
    var shortDescription: String?,
    var rule: String?,
    var imageUrl: String?,
    var minPlayers: Int?,
    var maxPlayers: Int?,
    var frontEndUrl: String?,
    var backEndUrl: String?,
    val createdOn: Instant?,
    var timesPlayed: Long? = null,
    var totalRating: Long? = null,
    var numberOfComments: Long? = null,
) {
    @DBRef
    var logs: MutableList<GameDevelopmentLog> = mutableListOf()

    fun toDomain(): GameRegistration =
        GameRegistration(
            GameRegistration.Id(id!!),
            uniqueName!!,
            displayName!!,
            shortDescription!!,
            rule!!,
            imageUrl!!,
            minPlayers!!,
            maxPlayers!!,
            frontEndUrl!!,
            backEndUrl!!,
            createdOn!!,
            totalRating,
            numberOfComments,
        )
}

fun GameRegistration.toData(): GameRegistrationData =
    GameRegistrationData(
        id = id?.value,
        uniqueName = uniqueName,
        displayName = displayName,
        shortDescription = shortDescription,
        rule = rule,
        imageUrl = imageUrl,
        minPlayers = minPlayers,
        maxPlayers = maxPlayers,
        frontEndUrl = frontEndUrl,
        backEndUrl = backEndUrl,
        createdOn = createdOn,
    )
