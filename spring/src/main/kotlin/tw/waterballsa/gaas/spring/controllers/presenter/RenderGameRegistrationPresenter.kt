package tw.waterballsa.gaas.spring.controllers.presenter

import tw.waterballsa.gaas.application.presenters.GameRegistrationPresenter
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.spring.controllers.viewmodel.GameRegistrationViewModel

class RenderGameRegistrationPresenter : GameRegistrationPresenter{var viewModel = emptyList<GameRegistrationViewModel>()
    private set

    override fun renderGameRegistrations(gameRegistrations: Collection<GameRegistration>) {
        viewModel = gameRegistrations.map { it.toViewModel() }
    }

    private fun GameRegistration.toViewModel(): GameRegistrationViewModel =
        GameRegistrationViewModel(
            id = id!!,
            name = displayName,
            img = imageUrl,
            minPlayers = minPlayers,
            maxPlayers = maxPlayers,
            createdOn = createdOn,
            rating = rating(),
            numberOfComments = numberOfComments ?: 0L
        )
}