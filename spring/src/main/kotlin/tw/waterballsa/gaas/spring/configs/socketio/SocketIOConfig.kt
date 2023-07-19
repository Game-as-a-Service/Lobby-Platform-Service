package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.SocketConfig
import com.corundumstudio.socketio.SocketIOServer
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SocketIOConfig (
){

    @Value("\${socketio.host}")
    private lateinit var host: String

    @Value("\${socketio.port}")
    private var port: Int = 0


    @Bean
    fun socketIOServer(): SocketIOServer {
        val configuration = com.corundumstudio.socketio.Configuration()
        configuration.hostname = host
        configuration.port = port

        return SocketIOServer(configuration)
    }
}