package tw.waterballsa.gaas.spring.eventbus

import com.corundumstudio.socketio.SocketIOServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.events.*
import tw.waterballsa.gaas.spring.controllers.viewmodel.SocketioViewModel



@Component
class WebSocketEventBus(
    val socketIOServer: SocketIOServer
) : EventBus {

    private val logger: Logger = LoggerFactory.getLogger(WebSocketEventBus::class.java)


    lateinit var viewModel: SocketioViewModel

    // TODO broadcast the events!
    override fun broadcast(events: Collection<DomainEvent>) {

        for (event in events) {
            if (event is PlayerJoinedRoomEvent) {
                val data = event.data
                val type = event.type
                socketIOServer.broadcastOperations.sendEvent(type.toString(), data)
            } else if(event is PlayerLeavedRoomEvent){
                val data = event.data
                val type = event.type
                socketIOServer.broadcastOperations.sendEvent(type.toString(), data)
            } else if(event is PlayerReadinessChangedEvent){
                val data = event.data
                val type = event.type
                socketIOServer.broadcastOperations.sendEvent(type.toString(), data)
            } else if(event is StartedGameEvent){
                val data = event.data
                val type = event.type
                socketIOServer.broadcastOperations.sendEvent(type.toString(), data)
            } else if(event is UserUpdatedEvent){
//                val data = event.data
//                val type = event.type
//                socketIOServer.broadcastOperations.sendEvent(type.toString(), data)
            }

        }


    }

}