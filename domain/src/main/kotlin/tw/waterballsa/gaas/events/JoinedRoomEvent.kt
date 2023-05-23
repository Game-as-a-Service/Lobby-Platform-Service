package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room

data class JoinedRoomEvent (
    val id: Room.Id,
    val name: String,
    val status: Room.Status,
    val gameRegistrationId: GameRegistration.Id,
    val host: Room.Player,
    val hostName: String,
    val playerIds: List<Room.Player>,
    val maxPlayers: Int,
    val minPlayers: Int,
    val isEncrypted: Boolean
) : DomainEvent()