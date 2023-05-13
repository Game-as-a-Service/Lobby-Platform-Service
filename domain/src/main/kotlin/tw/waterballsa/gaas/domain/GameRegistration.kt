package tw.waterballsa.gaas.domain

class GameRegistration(
    val id: Id? = null,
    val uniqueName: String,
    val displayName: String,
    val shortDescription: String,
    val rule: String,
    val imageUrl: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val frontEndUrl: String,
    val backEndUrl: String
) {
    @JvmInline
    value class Id(val value: String)
}
