package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.SocketIOServer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(SocketIOProperties::class)
class SocketIOConfig (){

    @Bean
    fun socketIOServer(socketIOProperties: SocketIOProperties): SocketIOServer {
        val configuration = com.corundumstudio.socketio.Configuration()
            .apply {
                with(socketIOProperties){
                    hostname = socketHost
                    port = socketPort
                } 
        }
        return SocketIOServer(configuration)
    }
}