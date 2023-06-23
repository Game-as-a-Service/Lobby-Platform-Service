package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.domain.Room
import javax.inject.Named

@Named
class GetRoomsUseCase(
    private val roomRepository: RoomRepository,
) {

    fun execute(request: Request, presenter: GetRoomsPresenter) =
        roomRepository.findByStatus(request.status, request.toPagination())
            .also { presenter.present(it) }

    class Request(
        val status: Room.Status,
        val page: Int,
        val offset: Int
    )

    interface GetRoomsPresenter {
        fun present(rooms: Pagination<Room>)
    }
}

private fun GetRoomsUseCase.Request.toPagination(): Pagination<Any> =
    Pagination(page, offset)
