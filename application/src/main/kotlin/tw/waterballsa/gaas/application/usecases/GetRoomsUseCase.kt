package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.GetRoomsEvent
import javax.inject.Named

@Named
class GetRoomsUseCase(
    private val roomRepository: RoomRepository,
    private val eventBus: EventBus
) {

    fun execute(request: Request, presenter: Presenter) =
        roomRepository.findByStatus(request.status, request.toPagination()).toGetRoomEvent()
            .also { event ->
                presenter.present(event)
                eventBus.broadcast(event)
            }

    class Request(
        val status: Room.Status,
        private val page: Int,
        private val offset: Int
    ) {
        fun toPagination(): Pagination<Any> = Pagination(page, offset)
    }
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

