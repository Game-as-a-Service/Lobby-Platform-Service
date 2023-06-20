package tw.waterballsa.gaas.application.model

class Pagination<T>(
    val page: Int,
    val offset: Int,
    val data: List<T> = emptyList()
)

