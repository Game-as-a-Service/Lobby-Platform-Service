package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.presenters.GameRegistrationPresenter
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import javax.inject.Named

@Named
class GetGameRegistrationsUsecase(
    private val gameRegistrationRepository: GameRegistrationRepository,
) {
    fun execute(request: Request, presenter: GameRegistrationPresenter) {
        presenter.renderGameRegistrations(gameRegistrationRepository.findGameRegistrations(request.sortBy))
    }

    data class Request(
        val sortBy: String?,
    )
}
