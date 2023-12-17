package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.enums.EventMessageType

abstract class RoomEvent(type: EventMessageType) : SocketIOResponseEvent(type) {
    abstract fun getRoomId(): Room.Id
}
