package tw.waterballsa.gaas.application.presenters

import tw.waterballsa.gaas.domain.Room

interface RoomPresenter {
    fun present(room: Room)
}
