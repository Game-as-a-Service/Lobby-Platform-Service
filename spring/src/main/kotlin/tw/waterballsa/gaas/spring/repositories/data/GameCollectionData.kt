package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import tw.waterballsa.gaas.domain.GameCollection
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User
import java.time.Instant

@Document
class GameCollectionData(
    @Id
    var id: String?,
    var gameId: String,
    var userId: String,
    var collectTime: Instant,
) {

    fun toDomain(): GameCollection = GameCollection(
        id?.let { GameCollection.Id(it) }, GameRegistration.Id(gameId), User.Id(userId), collectTime,
    )
}

fun GameCollection.toData(): GameCollectionData {
    return GameCollectionData(id?.value, gameId.value, userId.value, collectTime)
}