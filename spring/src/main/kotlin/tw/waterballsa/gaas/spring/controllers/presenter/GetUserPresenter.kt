package tw.waterballsa.gaas.spring.controllers.presenter

import tw.waterballsa.gaas.application.usecases.GetUserUseCase
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.controllers.viewmodel.GetUserViewModel

class GetUserPresenter : GetUserUseCase.Presenter {
    lateinit var viewModel: GetUserViewModel

    override fun present(user: User, currentRoom: Room?) {
        viewModel = user.toViewModel(currentRoom)
    }

    private fun User.toViewModel(currentRoom: Room?): GetUserViewModel =
        GetUserViewModel(
            id = id!!.value,
            email = email,
            nickname = nickname,
            lastPlayedGameId = lastPlayedGameId?.value,
            playedGamesIds = playedGamesIds?.map { it.value }?.toSet(),
            currentGameRoomId = currentRoom?.roomId?.value,
            currentGameUrl = currentRoom?.let { "${it.game.frontEndUrl}/games/${it.roomId?.value}" },
        )
}