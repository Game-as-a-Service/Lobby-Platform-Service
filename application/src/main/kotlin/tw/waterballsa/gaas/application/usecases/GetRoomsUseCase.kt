package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.GetRoomsEvent
import javax.inject.Named

@Named
class GetRoomsUseCase(
    private val roomRepository: RoomRepository,
) {

    fun execute(request: Request, presenter: Presenter) =
        roomRepository.findByStatus(request.status, request.toPagination())
            .toGetRoomEvent()
            .also { presenter.present(it) }

    class Request(
        val status: Room.Status,
        val page: Int,
        val offset: Int
    )
}

private fun Pagination<Room>.toGetRoomEvent(): GetRoomsEvent =
    GetRoomsEvent(
        rooms = data,
        GetRoomsEvent.Page(
            total = data.size,
            page = page,
            offset = offset
        )
    )

private fun GetRoomsUseCase.Request.toPagination(): Pagination<Any> =
    Pagination(page, offset)
