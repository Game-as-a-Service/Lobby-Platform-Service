package tw.waterballsa.gaas.spring.controllers.presenter

import tw.waterballsa.gaas.application.usecases.GetUserUseCase
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.controllers.viewmodel.GetUserViewModel

class GetUserPresenter : GetUserUseCase.Presenter {
    lateinit var viewModel: GetUserViewModel

    override fun present(user: User) {
        viewModel = user.toViewModel()
    }

    private fun User.toViewModel(): GetUserViewModel =
        GetUserViewModel(
            id = id!!.value,
            email = email,
            nickname = nickname,
            lastPlayedGameId = lastPlayedGameId,
        )
}