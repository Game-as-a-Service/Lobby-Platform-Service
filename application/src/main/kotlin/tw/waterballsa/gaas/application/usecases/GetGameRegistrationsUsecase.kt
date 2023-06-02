package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.domain.GameRegistration
import javax.inject.Named

@Named
class GetGameRegistrationsUsecase(
    private val gameRegistrationRepository: GameRegistrationRepository,
) {
    fun execute(presenter: Presenter) {
        presenter.renderGameRegistrations(gameRegistrationRepository.findGameRegistrations())
    }

    interface Presenter {
        fun renderGameRegistrations(gameRegistrations: Collection<GameRegistration>)
    }
}
