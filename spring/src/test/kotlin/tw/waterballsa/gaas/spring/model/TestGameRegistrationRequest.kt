package tw.waterballsa.gaas.spring.model

import tw.waterballsa.gaas.domain.GameRegistration

class TestGameRegistrationRequest(
    val uniqueName: String,
    val displayName: String,
    val shortDescription: String,
    val rule: String,
    val imageUrl: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val frontEndUrl: String,
    val backEndUrl: String
) {
    fun toGameRegistration(): GameRegistration =
        GameRegistration(
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
