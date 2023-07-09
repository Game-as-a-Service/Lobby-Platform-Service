package tw.waterballsa.gaas.spring.controllers.presenter

import tw.waterballsa.gaas.application.usecases.Presenter
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.events.UserUpdatedEvent
import tw.waterballsa.gaas.spring.controllers.viewmodel.UpdateUserViewModel
import tw.waterballsa.gaas.spring.extensions.getEvent

class UpdateUserPresenter : Presenter {
    lateinit var viewModel: UpdateUserViewModel
        private set

    override fun present(vararg events: DomainEvent) {
        viewModel = events.getEvent(UserUpdatedEvent::class)!!.toViewModel()
    }

    private fun UserUpdatedEvent.toViewModel(): UpdateUserViewModel =
        UpdateUserViewModel(id, email, nickname)

}