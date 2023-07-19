package tw.waterballsa.gaas.spring.repositories

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.listener.DataListener
import org.springframework.stereotype.Service
import tw.waterballsa.gaas.domain.Message
import tw.waterballsa.gaas.domain.MessageType
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.WalkingSkeleton
import java.util.logging.Logger

@Service
class SocketService {

    val log = Logger.getLogger("SocketService")


    fun sendMessage(room: String, eventName: String, senderClient: SocketIOClient, message: String) {
        for (client in senderClient.namespace.getRoomOperations(room).clients) {
            if (client.sessionId != senderClient.sessionId) {
                client.sendEvent(eventName, Message(MessageType.SERVER, message))
            }
        }
    }

    fun walk(value: String, eventName: String, senderClient: SocketIOClient) {

        val ki = senderClient.namespace.getRoomOperations(value).clients

        log.info(" afd$ki")
        var roomId: DataListener<WalkingSkeleton>? = null

        //return roomId
    }
}