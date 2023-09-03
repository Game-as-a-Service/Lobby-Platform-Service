package tw.waterballsa.gaas.spring.controllers.presenter

import tw.waterballsa.gaas.application.usecases.StartGameUseCase
import tw.waterballsa.gaas.spring.controllers.viewmodel.StartGameViewModel

class StartGamePresenter : StartGameUseCase.Presenter {
    lateinit var viewModel: StartGameViewModel

    override fun present(url: String) {
        viewModel = StartGameViewModel(url)
    }
}
