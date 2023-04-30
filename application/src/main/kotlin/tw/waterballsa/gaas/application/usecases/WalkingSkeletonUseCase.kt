package tw.waterballsa.gaas.application.usecases

import tw.waterballsa.gaas.application.repositories.WalkingSkeletonRepository
import javax.inject.Named

@Named
class WalkingSkeletonUseCase(
    private val walkingSkeletonRepository: WalkingSkeletonRepository
) {
    fun execute(): String {
        val skeleton = walkingSkeletonRepository.walk("Walk")
        return skeleton.walk()
    }
}
