package tw.waterballsa.gaas.spring.eventbus

import tw.waterballsa.gaas.events.DomainEvent
import kotlin.reflect.KClass

interface EventListener<T: DomainEvent> {
    val eventType: KClass<T>
    fun onEvents(events: List<T>)
}