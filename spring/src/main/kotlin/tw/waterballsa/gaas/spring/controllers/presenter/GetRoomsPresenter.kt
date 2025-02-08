package tw.waterballsa.gaas.spring.controllers.presenter

import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.application.usecases.GetRoomsUseCase
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.spring.controllers.viewmodel.GetRoomsViewModel

class GetRoomsPresenter : GetRoomsUseCase.GetRoomsPresenter {
    lateinit var viewModel: GetRoomsViewModel
        private set

    override fun present(rooms: Pagination<Room>) {
        viewModel = rooms.toViewModel()
    }

    private fun Pagination<Room>.toViewModel(): GetRoomsViewModel =
        GetRoomsViewModel(
            rooms = data.map { it.toRoomsViewModel() },
            page = toPage()
        )
}

private fun Room.toRoomsViewModel(): GetRoomsViewModel.RoomViewModel =
    GetRoomsViewModel.RoomViewModel(
        id = roomId!!.value,
        name = name,
        game = game.toGetRoomsView(),
        host = host.toGetRoomsView(),
        minPlayers = minPlayers,
        maxPlayers = maxPlayers,
        currentPlayers = players.size,
        isLocked = isLocked,
    )

private fun GameRegistration.toGetRoomsView(): GetRoomsViewModel.RoomViewModel.Game =
    GetRoomsViewModel.RoomViewModel.Game(id!!.value, displayName)

private fun Room.Player.toGetRoomsView(): GetRoomsViewModel.RoomViewModel.Player =
    GetRoomsViewModel.RoomViewModel.Player(id.value, nickname)

private fun Pagination<Room>.toPage(): GetRoomsViewModel.Page =
    GetRoomsViewModel.Page(
        page = page,
        offset = offset,
        total = total
    )
