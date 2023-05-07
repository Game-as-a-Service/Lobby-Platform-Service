package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.NotFoundException
import javax.inject.Named

@Named
class GetUserUseCase(
    private val userRepository: UserRepository
) {
    fun execute(id: User.UserId): User {
        return userRepository.findUserById(id)
            ?: throw NotFoundException("User not found: $id")
    }
}

