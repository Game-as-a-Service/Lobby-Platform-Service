package tw.waterballsa.gaas.application.presenters

import tw.waterballsa.gaas.domain.GameRegistration

interface GameRegistrationPresenter {
    fun renderGameRegistrations(gameRegistrations: Collection<GameRegistration>)
}
