package tw.waterballsa.gaas.spring.configs.socketio

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "socketio")
data class SocketIOProperties @ConstructorBinding constructor(
    val host: String = "127.0.0.1",
    val port: Int = 9001,
)