package tw.waterballsa.gaas.spring.extensions

import tw.waterballsa.gaas.events.DomainEvent
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

inline fun <reified T : DomainEvent> Collection<DomainEvent>.getEvent(type: KClass<T>): T? =
    firstNotNullOfOrNull { type.safeCast(it) }

inline fun <reified T : DomainEvent> Array<out DomainEvent>.getEvent(type: KClass<T>): T? =
    firstNotNullOfOrNull { type.safeCast(it) }
