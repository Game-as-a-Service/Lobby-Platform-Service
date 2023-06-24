package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import tw.waterballsa.gaas.domain.User

@Document
class UserData(
    @Id
    var id: String? = null,
    val email: String? = null,
    val nickname: String? = null,
    val identities: List<String>? = null,
) {

    fun toDomain(): User =
        User(
            User.Id(id!!),
            email!!,
            nickname!!,
            identities!!
        )
}

fun User.toData(): UserData =
    UserData(
        id = id?.value,
        email = email,
        nickname = nickname,
        identities = identities
    )
