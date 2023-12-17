package tw.waterballsa.gaas.spring.eventbus

import com.corundumstudio.socketio.SocketIOServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.events.RoomEvent
import tw.waterballsa.gaas.spring.controllers.viewmodel.SocketioViewModel
import kotlin.reflect.safeCast

@Component
class WebSocketEventBus(
    val socketIOServer: SocketIOServer
) : EventBus {

    private val logger: Logger = LoggerFactory.getLogger(WebSocketEventBus::class.java)

    lateinit var viewModel: SocketioViewModel
    override fun broadcast(events: Collection<DomainEvent>) {
        events.asSequence()
            .mapNotNull { RoomEvent::class.safeCast(it) }
            .forEach {
                socketIOServer.getRoomOperations("ROOM_${it.getRoomId().value}")
                    .sendEvent(it.type.eventName, it.getEventData())
            }
    }
}
