package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room.Id
import tw.waterballsa.gaas.domain.Room.Player

data class CreatedRoomEvent(
    val roomId: Id,
    val name: String,
    val game: GameRegistration,
    val host: Player,
    val currentPlayers: Int,
    val maxPlayers: Int,
    val minPlayers: Int,
    val isLocked: Boolean,
) : DomainEvent()
