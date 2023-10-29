package tw.waterballsa.gaas.spring.repositories

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.Room.*
import tw.waterballsa.gaas.domain.Room.Status.WAITING
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.exceptions.enums.PlatformError.USER_NOT_FOUND
import tw.waterballsa.gaas.spring.extensions.mapOrNull
import tw.waterballsa.gaas.spring.repositories.dao.RoomDAO
import tw.waterballsa.gaas.spring.repositories.data.RoomData
import tw.waterballsa.gaas.spring.repositories.data.RoomData.PlayerData
import tw.waterballsa.gaas.spring.repositories.data.toData

@Component
class SpringRoomRepository(
    private val roomDAO: RoomDAO,
    private val userRepository: UserRepository
) : RoomRepository {
    override fun createRoom(room: Room): Room = roomDAO.save(room.toData()).toDomain()

    override fun findById(roomId: Id): Room? = roomDAO.findById(roomId.value).mapOrNull { it.toDomain() }

    override fun deleteAll() {
        roomDAO.deleteAll()
    }

    override fun existsByHostId(hostId: User.Id): Boolean = roomDAO.existsByHostId(hostId.value)

    override fun update(room: Room): Room = roomDAO.save(room.toData()).toDomain(room.players)

    override fun findByStatus(status: Status, page: Pagination<Any>): Pagination<Room> =
        roomDAO.findByStatus(status, page.toPageable())
            .map { it.toDomain() }
            .toPagination()

    override fun closeRoom(room: Room) {
        roomDAO.deleteById(room.roomId!!.value)
    }

    override fun leaveRoom(room: Room) {
        roomDAO.save(room.toData())
    }

    override fun hasPlayerJoinedRoom(playerId: User.Id): Boolean =
        roomDAO.existsByPlayersIdIn(listOf(playerId.value))

    override fun findWaitingPublicRoomsByGame(game: GameRegistration): List<Room> {
        return roomDAO.findAllByStatusAndGameAndPasswordNull(WAITING, game.toData())
            .map { it.toDomain() }
    }

    private fun RoomData.toDomain(): Room =
        Room(
            roomId = Id(id!!),
            game = game.toDomain(),
            host = host.toDomain(),
            players = players.map(PlayerData::toDomain).toMutableList(),
            maxPlayers = maxPlayers,
            minPlayers = minPlayers,
            name = name,
            password = password,
            status = status
        )

    private fun User.Id.toRoomPlayer(): Player =
        userRepository.findById(this)
            ?.toRoomPlayer()
            ?: throw notFound(USER_NOT_FOUND, User::class).id(value)

    private fun User.Id.toPlayerData(): PlayerData =
        userRepository.findById(this)
            ?.let { PlayerData(it.id!!.value, it.nickname, false) }
            ?: throw notFound(USER_NOT_FOUND, User::class).id(value)
}

private fun User.toRoomPlayer(): Player =
    Player(
        id = Player.Id(id!!.value),
        nickname = nickname
    )

private fun Pagination<Any>.toPageable() = PageRequest.of(page, offset)

private fun <T> Page<T>.toPagination(): Pagination<T> =
    Pagination(pageable.pageNumber, pageable.pageSize, content)
