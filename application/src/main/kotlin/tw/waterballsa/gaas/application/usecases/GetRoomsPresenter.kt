package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.domain.Room

interface GetRoomsPresenter {
    fun present(rooms : Pagination<Room>)
}
