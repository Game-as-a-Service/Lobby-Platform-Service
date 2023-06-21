package tw.waterballsa.gaas.spring.controllers.presenter

import tw.waterballsa.gaas.application.usecases.Presenter
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.events.GetRoomsEvent
import tw.waterballsa.gaas.spring.controllers.viewmodel.GetRoomsViewModel
import tw.waterballsa.gaas.spring.extensions.getEvent

class GetRoomsPresenter : Presenter {
    lateinit var viewModel: GetRoomsViewModel

    override fun present(vararg events: DomainEvent) {
        viewModel = events.getEvent(GetRoomsEvent::class)!!.toViewModel()
    }

    private fun GetRoomsEvent.toViewModel(): GetRoomsViewModel =
        GetRoomsViewModel(
            rooms = rooms.map { it.toRoomsViewModel() },
            page = toPage(rooms.size)
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

fun GetRoomsEvent.toPage(size: Int): GetRoomsViewModel.Page =
    GetRoomsViewModel.Page(
        page = page.page,
        offset = page.offset,
        total = size
    )
