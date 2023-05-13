package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.events.DomainEvent

fun interface Presenter {
    fun present(vararg events: DomainEvent)
}
