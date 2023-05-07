package tw.waterballsa.gaas.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class WalkingSkeletonTest {

    @Test
    fun walk() {
        assertThat("Walk").isEqualTo(WalkingSkeleton("id", "Walk").walk())
    }
}
