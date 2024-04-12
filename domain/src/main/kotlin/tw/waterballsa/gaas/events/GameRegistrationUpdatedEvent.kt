package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.GameRegistration
import java.time.Instant

class GameRegistrationUpdatedEvent(
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
) : DomainEvent()
