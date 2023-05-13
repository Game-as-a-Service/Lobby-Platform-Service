package tw.waterballsa.gaas.domain

class WalkingSkeleton(
    val id: String? = null,
    val value: String,
) {
    fun walk(): String = value
}
