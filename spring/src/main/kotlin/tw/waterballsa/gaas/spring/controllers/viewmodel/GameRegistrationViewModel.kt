package tw.waterballsa.gaas.spring.controllers.viewmodel

import tw.waterballsa.gaas.domain.GameRegistration.Id
import java.time.Instant

data class GameRegistrationViewModel(
    val id: Id,
    val name: String,
    val img: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val createdOn: Instant,
    val rating: Double,
    val numberOfComments: Long,
)