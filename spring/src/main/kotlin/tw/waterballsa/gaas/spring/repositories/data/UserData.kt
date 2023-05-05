package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import tw.waterballsa.gaas.domain.User

@Document
class UserData(
    @Id
    private var id: String? = null,
    private var email: String? = null,
    private var nickname: String? = null
) {
    companion object {
        fun toData(user: User): UserData =
            with(user) {
                UserData(
                    id = id?.value,
                    email = email,
                    nickname = nickname
                )
            }
    }

    fun toDomain(): User =
        User(
            User.UserId(id!!),
            email!!,
            nickname!!
        )
}
