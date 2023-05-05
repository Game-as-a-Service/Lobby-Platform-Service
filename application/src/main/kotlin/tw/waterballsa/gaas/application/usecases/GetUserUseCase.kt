package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.UserRepository
import javax.inject.Named

@Named
class GetUserUseCase(
    private val userRepository: UserRepository
) {
    fun execute(id: String) = userRepository.findUserById(id)
}

