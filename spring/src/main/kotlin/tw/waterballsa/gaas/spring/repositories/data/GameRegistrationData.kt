package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import tw.waterballsa.gaas.domain.GameRegistration

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
    var backEndUrl: String?
) {
    companion object {
        fun toData(gameRegistration: GameRegistration): GameRegistrationData {
            val gameRegistrationData = with(gameRegistration) {
                GameRegistrationData(
                    uniqueName = uniqueName,
                    displayName = displayName,
                    shortDescription = shortDescription,
                    rule = rule,
                    imageUrl = imageUrl,
                    minPlayers = minPlayers,
                    maxPlayers = maxPlayers,
                    frontEndUrl = frontEndUrl,
                    backEndUrl = backEndUrl
                )
            }
            gameRegistration.id?.let { gameRegistrationData.id = it.value }
            return gameRegistrationData
        }
    }

    fun toDomain(): GameRegistration =
        GameRegistration(
            GameRegistration.GameRegistrationId(id!!),
            uniqueName!!,
            displayName!!,
            shortDescription!!,
            rule!!,
            imageUrl!!,
            minPlayers!!,
            maxPlayers!!,
            frontEndUrl!!,
            backEndUrl!!
        )
}
