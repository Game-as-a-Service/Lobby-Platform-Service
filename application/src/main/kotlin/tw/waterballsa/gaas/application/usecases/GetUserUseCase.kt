package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import javax.inject.Named

@Named
class GetUserUseCase(
    private val userRepository: UserRepository
) {
    fun execute(id: String): User? = userRepository.findUserById(id)
}

