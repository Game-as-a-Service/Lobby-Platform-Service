package tw.waterballsa.gaas.application.presenters

import tw.waterballsa.gaas.events.DomainEvent

fun interface Presenter {
    fun present(vararg events: DomainEvent)
}
