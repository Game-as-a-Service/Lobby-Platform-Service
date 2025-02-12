package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.model.Pageable
import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.query.RoomQuery
import tw.waterballsa.gaas.domain.Room
import javax.inject.Named

@Named
class GetRoomsUseCase(
    private val roomRepository: RoomRepository,
) {

    fun execute(request: Request, presenter: GetRoomsPresenter) =
        roomRepository.findByQuery(request.toRoomQuery(), request.toPageable())
            .also { presenter.present(it) }

    class Request(
        val status: Room.Status,
        val public: Boolean?,
        val keyword: String?,
        val page: Int,
        val offset: Int,
    )

    interface GetRoomsPresenter {
        fun present(rooms: Pagination<Room>)
    }
}

private fun GetRoomsUseCase.Request.toRoomQuery(): RoomQuery =
    RoomQuery(status, public, keyword)

private fun GetRoomsUseCase.Request.toPageable(): Pageable =
    Pageable(page, offset)
