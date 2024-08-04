package tw.waterballsa.gaas.spring.eventbus

import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.events.DomainEvent
import kotlin.reflect.safeCast

@Component
class DispatcherEventBus(
    private val listeners: List<EventListener<*>>,
) : EventBus {

    override fun broadcast(events: Collection<DomainEvent>) {
        listeners.forEach { listener ->
            events.mapNotNull { listener.eventType.safeCast(it) }
                .takeIf { it.isNotEmpty() }
                ?.run { (listener as EventListener<DomainEvent>).onEvents(this) }
        }
    }
}
