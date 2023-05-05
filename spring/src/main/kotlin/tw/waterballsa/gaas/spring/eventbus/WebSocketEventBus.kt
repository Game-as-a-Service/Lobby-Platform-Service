package tw.waterballsa.gaas.spring.eventbus

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.events.DomainEvent
import javax.inject.Named

@Named
class WebSocketEventBus : EventBus {
    override fun broadcast(events: Collection<DomainEvent>) {
        TODO("Not yet implemented")
    }
}