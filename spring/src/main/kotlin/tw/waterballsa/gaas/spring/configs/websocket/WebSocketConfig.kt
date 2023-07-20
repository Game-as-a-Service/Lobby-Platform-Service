package tw.waterballsa.gaas.spring.configs.websocket

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.*
import tw.waterballsa.gaas.spring.configs.DataHandler

//@Configuration
//@EnableWebSocketMessageBroker
//class WebSocketConfig : WebSocketMessageBrokerConfigurer {
//
//    override fun configureMessageBroker(config: MessageBrokerRegistry) {
//        config.enableSimpleBroker("/topic")
//        config.setApplicationDestinationPrefixes("/app")
//    }
//
//    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
//        registry.addEndpoint("/socket")
//            .setAllowedOrigins("*") // Allow connections from any origin
//            .withSockJS()
//    }
//
//}

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(getDataHandler(), "/data").setAllowedOrigins("*")
    }

    @Bean
    fun getDataHandler(): DataHandler {
        return DataHandler()
    }


}