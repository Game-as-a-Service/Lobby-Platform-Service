package tw.waterballsa.gaas.spring.controllers.presenter

import tw.waterballsa.gaas.application.presenters.RoomPresenter
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room

class CreateRoomPresenter : RoomPresenter {
    var viewModel: CreateRoomViewModel? = null
        private set

    override fun present(room: Room) {
        viewModel = room.toRoomsViewModel()
    }

}

data class CreateRoomViewModel(
    val id: Room.Id,
    val name: String,
    val game: Game,
    val host: Player,
    val isLocked: Boolean,
    val currentPlayers: Int,
    val maxPlayers: Int,
    val minPlayers: Int,
) {
    data class Game(val id: String, val name: String)
    data class Player(val id: String, val nickname: String)
}

private fun Room.toRoomsViewModel(): CreateRoomViewModel =
    CreateRoomViewModel(
        id = roomId!!,
        game = game.toView(),
        host = host.toView(),
        currentPlayers = players.size,
        maxPlayers = maxPlayers,
        minPlayers = minPlayers,
        name = name,
        isLocked = isLocked
    )

private fun GameRegistration.toView(): CreateRoomViewModel.Game =
    CreateRoomViewModel.Game(id!!.value, displayName)

private fun Room.Player.toView(): CreateRoomViewModel.Player =
    CreateRoomViewModel.Player(id.value, nickname)




