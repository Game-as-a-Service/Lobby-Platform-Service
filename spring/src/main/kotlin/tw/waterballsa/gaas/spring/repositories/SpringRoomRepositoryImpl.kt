package tw.waterballsa.gaas.spring.repositories

import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.ArrayElemAt.arrayOf
import org.springframework.data.mongodb.core.aggregation.ObjectOperators.valueOf
import org.springframework.data.mongodb.core.aggregation.SkipOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.model.Pageable
import tw.waterballsa.gaas.application.model.Pagination
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.application.repositories.query.RoomQuery
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.Room.Id
import tw.waterballsa.gaas.domain.Room.Status.WAITING
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.extensions.mapOrNull
import tw.waterballsa.gaas.spring.repositories.dao.RoomDAO
import tw.waterballsa.gaas.spring.repositories.data.RoomData
import tw.waterballsa.gaas.spring.repositories.data.toData

@Component
class SpringRoomRepository(
    private val roomDAO: RoomDAO,
    private val mongoTemplate: MongoTemplate,
) : RoomRepository {
    override fun createRoom(room: Room): Room = roomDAO.save(room.toData()).toDomain()

    override fun findById(roomId: Id): Room? = roomDAO.findById(roomId.value).mapOrNull { it.toDomain() }

    override fun deleteAll() = roomDAO.deleteAll()

    override fun existsByHostId(hostId: User.Id): Boolean = roomDAO.existsByHostId(hostId.value)

    override fun update(room: Room): Room = roomDAO.save(room.toData()).toDomain()

    override fun closeRoom(room: Room) = roomDAO.deleteById(room.roomId!!.value)

    override fun leaveRoom(room: Room) {
        roomDAO.save(room.toData())
    }

    override fun hasPlayerJoinedRoom(playerId: User.Id): Boolean =
        roomDAO.existsByPlayersIdIn(listOf(playerId.value))

    override fun findWaitingPublicRoomsByGame(game: GameRegistration): List<Room> {
        return roomDAO.findAllByStatusAndGameAndPasswordNull(WAITING, game.toData())
            .map { it.toDomain() }
    }

    override fun findByQuery(query: RoomQuery, pageable: Pageable): Pagination<Room> {
        val criteria =  Criteria.where("status").`is`(query.status)
        query.public?.run {
            if(this){
                criteria.and("password").isNull
            }else{
                criteria.and("password").ne(null)
            }
        }
        query.keyword?.run {
            criteria.orOperator(
                Criteria.where("name").regex(".*$this.*"),
                Criteria.where("game.displayName").regex(".*$this.*")
            )
        }
        val basePipeline = listOf(
            addFields()
                .addFieldWithValue("game", arrayOf(valueOf("game").toArray()).elementAt(1))
                .build(),
            lookup("gameRegistrationData", "game.v", "_id", "game"),
            addFields()
                .addFieldWithValue("game", arrayOf("game").elementAt(0))
                .build(),
            match(criteria),
        )
        val data = mongoTemplate.aggregate(
            newAggregation(
                *basePipeline.toTypedArray(),
                limit(pageable.offset.toLong()),
                SkipOperation((pageable.page * pageable.offset).toLong() )
            ),
            "roomData",
            RoomData::class.java
        ).mappedResults.map { it.toDomain() }

        val count = mongoTemplate.aggregate(
            newAggregation(
                *basePipeline.toTypedArray(),
                count().`as`("count")
            ),
            "roomData",
            Document::class.java
        ).mappedResults[0].getInteger("count")


        return Pagination(pageable.page, pageable.offset, count, data)
    }

    override fun findUserCurrentRoom(playerId: User.Id): Room? {
        return roomDAO.findOneByPlayersIdIn(listOf(playerId.value))?.toDomain()
    }
}