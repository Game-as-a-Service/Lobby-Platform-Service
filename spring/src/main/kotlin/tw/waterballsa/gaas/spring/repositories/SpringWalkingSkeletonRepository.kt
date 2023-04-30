package tw.waterballsa.gaas.spring.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.repositories.WalkingSkeletonRepository
import tw.waterballsa.gaas.domain.WalkingSkeleton
import tw.waterballsa.gaas.spring.repositories.dao.WalkingSkeletonDAO
import tw.waterballsa.gaas.spring.repositories.data.WalkingSkeletonData

@Component
class SpringWalkingSkeletonRepository(
    private val walkingSkeletonDAO: WalkingSkeletonDAO
) : WalkingSkeletonRepository {
    override fun walk(value: String): WalkingSkeleton {
        val data = WalkingSkeletonData(value = value)
        return walkingSkeletonDAO.save(data).toDomain()
    }
}
