package tw.waterballsa.gaas.spring.eventbus

import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.events.DomainEvent

@Component
class WebSocketEventBus : EventBus {

    // TODO broadcast the events!
    override fun broadcast(events: Collection<DomainEvent>) {
    }
}