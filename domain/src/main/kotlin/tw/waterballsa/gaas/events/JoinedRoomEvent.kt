package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room

data class JoinedRoomEvent (
    val message: String,
) : DomainEvent()