package tw.waterballsa.gaas.spring.controllers.presenter

import tw.waterballsa.gaas.application.presenters.RoomPresenter
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.spring.controllers.viewmodel.FastJoinRoomViewModel

class FastJoinRoomPresenter: RoomPresenter {
    lateinit var viewModel: FastJoinRoomViewModel
        private set
    override fun present(room: Room) {
        viewModel = FastJoinRoomViewModel(room.roomId!!.value)
    }
}