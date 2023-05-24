package tw.waterballsa.gaas.spring.models

class TestJoinRoomRequest(
    val password: String? = null,
) {
    fun toJson(): String {
        val password = this.password ?: ""
        return """
            {
                "password": "$password"
            }
        """.trimIndent()
    }
}