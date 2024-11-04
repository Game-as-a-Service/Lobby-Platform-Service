package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User

@Document
class UserData(
    @Id
    var id: String? = null,
    val email: String = "",
    val nickname: String = "",
    val identities: List<String> = emptyList(),
    val lastPlayedGameId: String? = null,
    val playedGamesIds: Set<String>? = null,
) {

    fun toDomain(): User =
        User(
            User.Id(id!!),
            email,
            nickname,
            identities.toMutableList(),
            lastPlayedGameId?.let { GameRegistration.Id(it) },
            playedGamesIds?.map { GameRegistration.Id(it) }?.toSet(),
        )
}

fun User.toData(): UserData =
    UserData(
        id = id?.value,
        email = email,
        nickname = nickname,
        identities = identities,
        lastPlayedGameId = lastPlayedGameId?.value,
        playedGamesIds = playedGamesIds?.map { it.value }?.toSet(),
    )
