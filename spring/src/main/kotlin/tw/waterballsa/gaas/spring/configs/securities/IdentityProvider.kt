package tw.waterballsa.gaas.spring.configs.securities

enum class IdentityProvider(val queryParam: String) {
    GOOGLE("google-oauth2"),
    GITHUB("github"),
    LINKEDIN("linkedin"),
    DISCORD("discord")
}
