package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.WalkingSkeleton

interface WalkingSkeletonRepository {
    fun walk(value: String): WalkingSkeleton
}
