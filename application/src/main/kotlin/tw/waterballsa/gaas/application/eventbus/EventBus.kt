package tw.waterballsa.gaas.application.eventbus

import tw.waterballsa.gaas.events.DomainEvent

interface EventBus {
    fun broadcast(vararg events: DomainEvent) {
        broadcast(events.toList())
    }

    fun broadcast(events: Collection<DomainEvent>)
}
