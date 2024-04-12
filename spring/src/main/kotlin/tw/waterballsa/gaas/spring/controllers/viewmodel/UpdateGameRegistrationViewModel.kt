package tw.waterballsa.gaas.spring.controllers.viewmodel

import tw.waterballsa.gaas.domain.GameRegistration
import java.time.Instant

data class UpdateGameRegistrationViewModel(
    val id: GameRegistration.Id,
    val uniqueName: String,
    val displayName: String,
    val shortDescription: String,
    val rule: String,
    val imageUrl: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val frontEndUrl: String,
    val backEndUrl: String,
    val createdOn: Instant,
)
