package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.mongodb.core.mapping.Document
import tw.waterballsa.gaas.domain.WalkingSkeleton

@Document
class WalkingSkeletonData(var id: String? = null, var value: String? = null) {
    fun toDomain(): WalkingSkeleton = WalkingSkeleton(id, value!!)
}

