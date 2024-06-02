package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.domain.GameRegistration
import javax.inject.Named

@Named
class GetGameRegistrationsUsecase(
    private val gameRegistrationRepository: GameRegistrationRepository,
) {
    fun execute(request: Request, presenter: Presenter) {
        presenter.renderGameRegistrations(gameRegistrationRepository.findGameRegistrations(request.sortBy))
    }

    interface Presenter {
        fun renderGameRegistrations(gameRegistrations: Collection<GameRegistration>)
    }

    data class Request(
        val sortBy: String?,
    )
}
