package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.presenters.GameRegistrationPresenter
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import javax.inject.Named

@Named
class GetGameCollectionsUseCase(
    userRepository: UserRepository,
    gameRegistrationRepository: GameRegistrationRepository,
) : AbstractGameCollectionUseCase(userRepository, gameRegistrationRepository) {

    fun execute(request: Request, presenter: GameRegistrationPresenter) {
        val user = findUser(request.identityProviderId)
        val collectGameRegistrations = gameRegistrationRepository.findCollectGameRegistrations(user.id!!)
        presenter.renderGameRegistrations(collectGameRegistrations)
    }

    data class Request(
        val identityProviderId: String,
    )
}
