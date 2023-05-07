package tw.waterballsa.gaas.spring.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.Room.Id
import tw.waterballsa.gaas.domain.Room.Player
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.extensions.orElseNull
import tw.waterballsa.gaas.spring.repositories.dao.GameRegistrationDAO
import tw.waterballsa.gaas.spring.repositories.dao.RoomDAO
import tw.waterballsa.gaas.spring.repositories.dao.UserDAO
import tw.waterballsa.gaas.spring.repositories.data.GameRegistrationData
import tw.waterballsa.gaas.spring.repositories.data.RoomData.Companion.toData
import tw.waterballsa.gaas.spring.repositories.data.UserData

@Component
class SpringRoomRepository(
    private val roomDAO: RoomDAO,
    private val gameRegistrationDAO: GameRegistrationDAO,
    private val userDAO: UserDAO
) : RoomRepository {
    override fun createRoom(room: Room): Room = roomDAO.save(room.toData()).toDomain(room.gameRegistration, room.host)

    override fun findByRoomId(roomId: Id): Room? {
        val room = roomDAO.findById(roomId.value).orElseNull()
        return room?.run {
            val player = userDAO.findById(hostId!!).map { it.toRoomPlayer() }.orElseNull()
            val gameRegistration =
                gameRegistrationDAO.findById(gameRegistrationId!!).map(GameRegistrationData::toDomain).orElseNull()
            toDomain(gameRegistration!!, player!!)
        }
    }

    override fun deleteAll() {
        roomDAO.deleteAll()
    }

    override fun existsByHostId(userId: User.UserId): Boolean = roomDAO.existsByHostId(userId.value)
}

private fun UserData.toRoomPlayer(): Player =
    Player(
        userId = User.UserId(id!!),
        nickname = nickname!!
    )
