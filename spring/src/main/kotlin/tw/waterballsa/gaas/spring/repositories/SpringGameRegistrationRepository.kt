package tw.waterballsa.gaas.spring.repositories

import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.aggregation.ConvertOperators
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.GameRegistration.Id
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.extensions.mapOrNull
import tw.waterballsa.gaas.spring.repositories.dao.GameRegistrationDAO
import tw.waterballsa.gaas.spring.repositories.data.GameRegistrationData
import tw.waterballsa.gaas.spring.repositories.data.toData

@Component
class SpringGameRegistrationRepository(
    private val gameRegistrationDAO: GameRegistrationDAO,
    private val mongoTemplate: MongoTemplate,
) : GameRegistrationRepository {
    override fun registerGame(gameRegistration: GameRegistration): GameRegistration =
        gameRegistrationDAO.save(gameRegistration.toData()).toDomain()

    override fun findGameRegistrationByUniqueName(uniqueName: String): GameRegistration? =
        gameRegistrationDAO.findByUniqueName(uniqueName)?.toDomain()

    override fun deleteAll() {
        gameRegistrationDAO.deleteAll()
    }

    override fun getNumberOfTotalGameRegistrations(): Long = gameRegistrationDAO.count()

    override fun existsByUniqueName(uniqueName: String): Boolean = gameRegistrationDAO.existsByUniqueName(uniqueName)

    override fun findGameRegistrations(sortBy: String?): List<GameRegistration> {
        val agg =
            SortBy.from(sortBy)?.let {
                newAggregation(
                    addFields()
                        .addField("rating").withValueOfExpression("totalRating / numberOfComments")
                        .build(),
                    sort(Sort.by(it.orders))
                )
            } ?: newAggregation(
                addFields()
                    .addField("rating").withValueOfExpression("totalRating / numberOfComments")
                    .build(),
            )
        return mongoTemplate.aggregate(agg, "gameRegistrationData", GameRegistrationData::class.java)
            .mappedResults.map { it.toDomain() }
    }

    override fun findById(id: Id): GameRegistration? =
        gameRegistrationDAO.findById(id.value).mapOrNull(GameRegistrationData::toDomain)

    override fun updateGame(gameRegistration: GameRegistration): GameRegistration =
        gameRegistrationDAO.save(gameRegistration.toData()).toDomain()

    override fun findCollectGameRegistrations(userId: User.Id): List<GameRegistration> {
        val aggregation = newAggregation(
            match(Criteria.where("userId").`is`(userId.value)),
            sort(Sort.Direction.DESC, "collectTime"),
            addFields()
                .addField("gameObjectId").withValue(ConvertOperators.ToObjectId.toObjectId("\$gameId"))
                .build(),
            lookup(
                "gameRegistrationData",
                "gameObjectId",
                "_id",
                "gameRegistration"
            ),
            unwind("gameRegistration"),
            replaceRoot("gameRegistration"),
        )

        return mongoTemplate.aggregate(
            aggregation,
            "gameCollectionData",
            GameRegistrationData::class.java
        ).map { it.toDomain() }
    }

    enum class SortBy(val value: String, val orders: List<Order>) {
        CREATED_ON("createdOn", listOf(Order.desc("createdOn"), Order.desc("_id"))),
        TIMES_PLAYED("timesPlayed", listOf(Order.desc("timesPlayed"), Order.desc("_id"))),
        RATING("rating", listOf(Order.desc("ratio"), Order.desc("_id")))
        ;

        companion object {
            private val map = SortBy.values().associateBy { it.value }
            infix fun from(value: String?): SortBy? = value?.let { map[value] }
        }
    }
}
