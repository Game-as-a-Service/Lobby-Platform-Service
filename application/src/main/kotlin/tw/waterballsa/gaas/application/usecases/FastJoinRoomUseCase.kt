package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.presenters.RoomPresenter
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.GameRegistration.Id
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.enums.PlatformError.GAME_NOT_FOUND
import java.util.*
import javax.inject.Named

@Named
class FastJoinRoomUseCase(
    roomRepository: RoomRepository,
    userRepository: UserRepository,
    private val gameRegistrationRepository: GameRegistrationRepository,
    private val createRoomUsecase: CreateRoomUsecase,
    private val joinRoomUseCase: JoinRoomUsecase,
) : AbstractRoomUseCase(roomRepository, userRepository) {

    fun execute(request: Request, presenter: RoomPresenter) {
        with(request) {
            val gameRegistration = findGameRegistration()
            findWaitingPublicRoomsByGame(gameRegistration)
                ?.joinRoom(userIdentity, presenter)
                ?: createRoom(gameRegistration, presenter)
        }
    }

    private fun Request.findGameRegistration(): GameRegistration =
        gameRegistrationRepository.findById(Id(gameId))
            ?: throw notFound(GAME_NOT_FOUND, GameRegistration::class).id(gameId)

    private fun findWaitingPublicRoomsByGame(gameRegistration: GameRegistration): Room? =
        roomRepository.findWaitingPublicRoomsByGame(gameRegistration)
            .firstOrNull { !it.isFull() }

    private fun Room.joinRoom(userIdentity: String, presenter: RoomPresenter) {
        joinRoomUseCase.execute(
            JoinRoomUsecase.Request(
                roomId = roomId!!.value,
                userIdentity,
            ),
        )
        presenter.present(this)
    }

    private fun Request.createRoom(game: GameRegistration, presenter: RoomPresenter) {
        createRoomUsecase.execute(
            CreateRoomUsecase.Request(
                name = "fast-join-${UUID.randomUUID()}",
                gameId = game.id!!.value,
                userIdentity = userIdentity,
                minPlayers = game.minPlayers,
                maxPlayers = game.maxPlayers,
            ),
            presenter,
        )
    }

    data class Request(
        val gameId: String,
        val userIdentity: String,
    )
}
