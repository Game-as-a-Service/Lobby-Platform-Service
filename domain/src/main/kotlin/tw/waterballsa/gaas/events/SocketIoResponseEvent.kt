package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.events.enums.EventMessageType

abstract class SocketIoResponseEvent(val type: EventMessageType) : DomainEvent(), SocketIoResponseData
