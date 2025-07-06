package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.application.model.Pageable
import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.application.repositories.query.RoomQuery
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User

interface RoomRepository {
    fun deleteAll()
    fun createRoom(room: Room): Room
    fun findById(roomId: Room.Id): Room?
    fun existsByHostId(hostId: User.Id): Boolean
    fun update(room: Room): Room
    fun closeRoom(room: Room)
    fun leaveRoom(room: Room)
    fun hasPlayerJoinedRoom(playerId: User.Id): Boolean
    fun findWaitingPublicRoomsByGame(game: GameRegistration): List<Room>
    fun findByQuery(query: RoomQuery, pageable: Pageable): Pagination<Room>
    fun findUserCurrentRoom(playerId: User.Id): Room?
}
