package tw.waterballsa.gaas.domain

import lombok.Data

@Data
class WalkingSkeleton(
    val id: String? = null,
    val value: String,


) {

    constructor(value: String) : this("", value)


    fun walk(): String = value
}
