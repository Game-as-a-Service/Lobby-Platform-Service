package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.NotFoundException.Companion.notFound
import javax.inject.Named

@Named
class GetUserUseCase(
    private val userRepository: UserRepository,
) {
    fun execute(id: User.Id): User =
        userRepository.findById(id) ?: throw notFound(User::class).id(id)
}
