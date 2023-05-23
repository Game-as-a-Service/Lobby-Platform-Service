package tw.waterballsa.gaas.spring.models

class TestJoinRoomRequest(
    val userId: String,
    val password: String? = null,
) {
    fun toJson(): String {
        val password = this.password ?: ""
        return """
            {
                "userId": "$userId",
                "password": "$password"
            }
        """.trimIndent()
    }
}