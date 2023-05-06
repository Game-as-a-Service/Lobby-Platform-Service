package tw.waterballsa.gaas.exceptions

import tw.waterballsa.gaas.domain.User

class UserNotFoundException(id: User.UserId) : Throwable() {
    override val message: String = "User not found: $id"
}
