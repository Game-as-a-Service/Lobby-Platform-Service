package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.annotation.OnConnect
import com.corundumstudio.socketio.annotation.OnDisconnect
import com.corundumstudio.socketio.annotation.OnEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.spring.configs.socketio.event.SocketIOChatEvent
import tw.waterballsa.gaas.spring.configs.socketio.event.SocketIORoomEvent
import java.time.Instant


@Component
class SocketIOEventHandler(
    private val socketIOServer: SocketIOServer,
    private val eventBus: EventBus,
    protected val roomRepository: RoomRepository,
    protected val userRepository: UserRepository,
) {

    private val logger: Logger = LoggerFactory.getLogger(SocketIOEventHandler::class.java)


    @OnConnect
    fun onConnect(client: SocketIOClient){
        val token = client.handshakeData.getSingleUrlParam("token")
        logger.info("user connect, SessionId: {}, token: {}", client.sessionId, token)
    }

    @OnDisconnect
    fun onDisconnect(client: SocketIOClient){
        val token = client.handshakeData.getSingleUrlParam("token")
        logger.info("user disconnect, SessionId: {}, token: {}", client.sessionId, token)
        client.disconnect()
    }

    @OnEvent(value = SocketIOEventName.JOIN_ROOM)
    fun onJoinRoom(client: SocketIOClient, event: SocketIORoomEvent, ackRequest: AckRequest){
        client.joinRoom(event.target)
        logger.info("Client joined room: ${event.target}")
        logger.info("id = " + event.user.id + " nickname " + event.user.nickname +  " targetRoom  " + event.target)
        logger.info(" room size is : ${client.getCurrentRoomSize(event.target)}")
    }

    @OnEvent(value = SocketIOEventName.LEAVE_ROOM)
    fun onLeaveRoom(client: SocketIOClient, event: SocketIORoomEvent, ackRequest: AckRequest){
        logger.info(" LEAVE_ROOM Received message: ${event.target} from client: ${client.sessionId}")
        client.leaveRoom(event.target)
    }

    @OnEvent(value = SocketIOEventName.CHAT_MESSAGE)
    fun onChatMessage(client: SocketIOClient, event: SocketIOChatEvent, ackRequest: AckRequest){
        event.timestamp = Instant.now().toString()
        val room = if(event.isLobby()){
            socketIOServer.broadcastOperations
        }else{
            socketIOServer.getRoomOperations(event.target)
        }
        room.sendEvent(SocketIOEventName.CHAT_MESSAGE, event)
    }
}
