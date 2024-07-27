package tw.waterballsa.gaas.spring.eventbus

import com.corundumstudio.socketio.SocketIOServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.events.RoomEvent
import kotlin.reflect.KClass

@Component
class RoomEventListener(
    override val eventType: KClass<RoomEvent>,
    val socketIOServer: SocketIOServer,
): EventListener<RoomEvent> {

    @Autowired
    constructor(socketIOServer: SocketIOServer): this(RoomEvent::class, socketIOServer)

    override fun onEvents(events: List<RoomEvent>) {
        events
            .forEach {
                socketIOServer.getRoomOperations("ROOM_${it.getRoomId().value}")
                    .sendEvent(it.type.eventName, it.getEventData())
            }
    }
}