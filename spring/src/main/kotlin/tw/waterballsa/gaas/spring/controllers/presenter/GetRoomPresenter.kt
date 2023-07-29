package tw.waterballsa.gaas.spring.controllers.presenter

import tw.waterballsa.gaas.application.usecases.GetRoomUsecase
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.spring.controllers.viewmodel.GetRoomViewModel

class GetRoomPresenter : GetRoomUsecase.Presenter {
    lateinit var viewModel: GetRoomViewModel
        private set

    override fun present(room: Room) {
        viewModel = room.toViewModel()
    }

}

private fun Room.toViewModel(): GetRoomViewModel =
    GetRoomViewModel(
        id = roomId!!.value,
        name = name,
        game = game.toViewModel(),
        host = host.toViewModel(),
        players = players.map { it.toViewModel() },
        maxPlayers = maxPlayers,
        minPlayers = minPlayers,
        currentPlayers = players.size,
        isLocked = isLocked,
        status = status.toString()
    )

private fun GameRegistration.toViewModel(): GetRoomViewModel.Game =
    GetRoomViewModel.Game(id!!.value, displayName)

private fun Room.Player.toViewModel(): GetRoomViewModel.Player =
    GetRoomViewModel.Player(id.value, nickname, readiness)