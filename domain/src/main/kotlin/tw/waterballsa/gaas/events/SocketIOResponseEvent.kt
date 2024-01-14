package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.events.enums.EventMessageType

abstract class SocketIOResponseEvent(val type: EventMessageType) : DomainEvent() {
    abstract fun getEventData(): Any
}
