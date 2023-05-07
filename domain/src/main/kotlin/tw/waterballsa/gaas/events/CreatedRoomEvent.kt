package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room.*

data class CreatedRoomEvent(
    val roomId: Id,
    val gameRegistrationId: GameRegistration.GameRegistrationId,
    val host: Player,
    val hostName: String,
    val playerIds: List<Player>,
    val maxPlayers: Int,
    val minPlayers: Int,
    val name: String,
    val description: String,
    val status: Status,
    val isEncrypted: Boolean
) : DomainEvent()
