package tw.waterballsa.gaas.spring.repositories.dao

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import org.springframework.stereotype.Repository
import tw.waterballsa.gaas.spring.repositories.data.UserData

@Repository
interface UserDAO : MongoRepository<UserData, String> {
    fun existsByEmail(email: String): Boolean
    fun existsByNickname(nickname: String): Boolean
    fun findByEmail(email: String): UserData?
    fun findByIdentities(identityProviderId: String): UserData?

    @Query("{ '_id' : ?0 }")
    @Update("{ '\$set': { 'lastPlayedGameId': ?1 }, '\$addToSet': { 'playedGamesIds': ?1 } }")
    fun setLastPlayedGameIdAndAddToSetPlayedGamesIdsById(id: String, gameId: String)
}
