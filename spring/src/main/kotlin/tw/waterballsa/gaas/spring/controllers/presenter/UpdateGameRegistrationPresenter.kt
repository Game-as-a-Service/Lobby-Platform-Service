package tw.waterballsa.gaas.spring.controllers.presenter

import tw.waterballsa.gaas.application.usecases.Presenter
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.events.GameRegistrationUpdatedEvent
import tw.waterballsa.gaas.spring.controllers.viewmodel.UpdateGameRegistrationViewModel
import tw.waterballsa.gaas.spring.extensions.getEvent

class UpdateGameRegistrationPresenter : Presenter {

    lateinit var viewModel: UpdateGameRegistrationViewModel
        private set

    override fun present(vararg events: DomainEvent) {
        viewModel = events.getEvent(GameRegistrationUpdatedEvent::class)!!.toViewModel()
    }

    private fun GameRegistrationUpdatedEvent.toViewModel(): UpdateGameRegistrationViewModel =
        UpdateGameRegistrationViewModel(
            id,
            uniqueName,
            displayName,
            shortDescription,
            rule,
            imageUrl,
            minPlayers,
            maxPlayers,
            frontEndUrl,
            backEndUrl
        )

}
