package tw.waterballsa.gaas.spring.configs.socketio

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "socketio")
class SocketIOProperties (
    val host: String = "127.0.0.1",
    val port: Int = 9001){
}