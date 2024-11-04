package tw.waterballsa.gaas.spring.repositories.dao

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import org.springframework.stereotype.Repository
import tw.waterballsa.gaas.spring.repositories.data.GameRegistrationData

@Repository
interface GameRegistrationDAO : MongoRepository<GameRegistrationData, String> {
    fun findByUniqueName(uniqueName: String): GameRegistrationData?
    fun existsByUniqueName(uniqueName: String): Boolean

    @Query("{ '_id' : ?0 }")
    @Update("{ '\$inc' : { 'timesPlayed' : ?1 } }")
    fun incrementTimesPlayedById(id: String, increment: Long = 1)

    @Query("{ '_id' : ?0 }")
    @Update("{ '\$inc' : { 'totalRating' : ?1, 'numberOfComments' : ?2 } }")
    fun incrementTotalRatingAndNumberOfCommentsById(id: String, totalRating: Long, numberOfComments: Long)
}
