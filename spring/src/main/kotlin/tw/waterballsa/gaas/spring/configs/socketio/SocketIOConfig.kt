package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.AuthorizationListener
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.DefaultExceptionListener
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource


@Configuration
@EnableConfigurationProperties(SocketIOProperties::class)
class SocketIOConfig{

    @Bean
    fun socketIOServer(socketIOProperties: SocketIOProperties): SocketIOServer {
        val configuration = com.corundumstudio.socketio.Configuration()
            .apply {
                with(socketIOProperties){
                    hostname = socketHost
                    port = socketPort
                    allowHeaders = "Authorization, CustomHeaderName"
                }
        }


        return SocketIOServer(configuration)
    }

}