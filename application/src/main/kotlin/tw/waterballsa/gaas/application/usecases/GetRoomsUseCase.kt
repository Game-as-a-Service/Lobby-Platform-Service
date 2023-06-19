package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.model.Page
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.domain.Room
import javax.inject.Named

@Named
class GetRoomsUseCase(
    private val roomRepository: RoomRepository
) {

    fun execute(request: Request): List<Room> =
        roomRepository.findByStatusThenPageable(request.toRoomStatus(), request.toPage())

    class Request(
        private val status: String,
        private val page: Int,
        private val perPage: Int
    ) {
        fun toPage(): Page = Page(page, perPage)
        fun toRoomStatus(): Room.Status = Room.Status.valueOf(status)
    }
}
