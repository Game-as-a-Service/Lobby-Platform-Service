package tw.waterballsa.gaas.spring.controllers.presenter

import tw.waterballsa.gaas.application.usecases.GetUserMeUseCase
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.controllers.viewmodel.GetUserMeViewModel

class GetUserMePresenter : GetUserMeUseCase.Presenter {
    lateinit var viewModel: GetUserMeViewModel

    override fun present(user: User) {
        viewModel = user.toViewModel()
    }

    private fun User.toViewModel(): GetUserMeViewModel =
        GetUserMeViewModel(
            id = id!!.value,
            email = email,
            nickname = nickname,
        )
}