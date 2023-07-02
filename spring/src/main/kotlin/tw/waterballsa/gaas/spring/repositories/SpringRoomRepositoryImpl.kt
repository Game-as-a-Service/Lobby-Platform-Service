package tw.waterballsa.gaas.spring.repositories

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.Room.*
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import tw.waterballsa.gaas.spring.extensions.mapOrNull
import tw.waterballsa.gaas.spring.repositories.dao.RoomDAO
import tw.waterballsa.gaas.spring.repositories.data.RoomData
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

    override fun findByStatus(status: Status, page: Pagination<Any>): Pagination<Room> {
        return roomDAO.findByStatus(status, page.toPageable())
            .map { it.toDomain() }
            .toPagination()
    }

    private fun RoomData.toDomain(): Room =
        Room(
            roomId = Id(id!!),
            game = game.toDomain(),
            host = host.toDomain(),
            players = players.map(RoomData.PlayerData::toDomain).toMutableList(),
            maxPlayers = maxPlayers,
            minPlayers = minPlayers,
            name = name,
            password = password,
        )

    private fun User.Id.toRoomPlayer(): Player =
        userRepository.findById(this)
            ?.toRoomPlayer()
            ?: throw notFound(User::class).id(value)
}

private fun User.toRoomPlayer(): Player =
    Player(
        id = Player.Id(id!!.value),
        nickname = nickname
    )

private fun Pagination<Any>.toPageable() = PageRequest.of(page, offset)

private fun <T> Page<T>.toPagination(): Pagination<T> =
    Pagination(pageable.pageNumber, pageable.pageSize, content)
