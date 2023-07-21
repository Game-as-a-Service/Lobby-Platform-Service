package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.domain.GameRegistration
import javax.inject.Named

@Named
class UpdateGameRegistrationUseCase(
) {

    fun execute(request: Request, presenter: Presenter) {
        TODO("Not yet implemented")
    }

    data class Request(
        val gameId: GameRegistration.Id,
        val uniqueName: String,
        val displayName: String,
        val shortDescription: String,
        val rule: String,
        val imageUrl: String,
        val minPlayers: Int,
        val maxPlayers: Int,
        val frontEndUrl: String,
        val backEndUrl: String,
    )

}
