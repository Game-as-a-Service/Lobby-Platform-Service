package tw.waterballsa.gaas.spring.configs.socketio

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "socketio")
data class SocketIOProperties (
    val socketHost: String = "127.0.0.1",
    val socketPort: Int = 9001){
}