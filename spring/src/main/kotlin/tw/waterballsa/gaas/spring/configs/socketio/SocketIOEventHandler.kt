package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.annotation.OnConnect
import com.corundumstudio.socketio.annotation.OnDisconnect
import com.corundumstudio.socketio.annotation.OnEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.spring.configs.socketio.event.SocketIOChatEvent
import tw.waterballsa.gaas.spring.configs.socketio.event.SocketIOHealthCheckResponse
import tw.waterballsa.gaas.spring.configs.socketio.event.SocketIORoomEvent
import java.time.Instant


@Component
class SocketIOEventHandler(
    private val socketIOServer: SocketIOServer,
    private val jwtDecoder: JwtDecoder,
    protected val userRepository: UserRepository,
) {
    companion object{
        private const val USER_PREFIX = "USER_"
    }


    private val logger: Logger = LoggerFactory.getLogger(SocketIOEventHandler::class.java)

    @OnConnect
    fun onConnect(client: SocketIOClient){
        val userId = client.handshakeData.getSingleUrlParam("token")
            ?.toJwt()?.subject
            ?.let { userRepository.findByIdentity(it) }
            ?.id?.value

        if(userId == null){
            client.disconnect()
        }else{
            client.joinRoom("$USER_PREFIX$userId")
            logger.info("user connect, SessionId: {}, UserId: {}", client.sessionId, userId)
        }
    }

    @OnDisconnect
    fun onDisconnect(client: SocketIOClient){
        val userId = client.userId()
        client.disconnect()
        logger.info("user disconnect, SessionId: {}, UserId: {}", client.sessionId, userId)
    }

    @OnEvent(value = SocketIOEventName.JOIN_ROOM)
    fun onJoinRoom(client: SocketIOClient, event: SocketIORoomEvent, ackRequest: AckRequest){
        val userId = client.userId()
        client.joinRoom(event.target)
        logger.info("user join room, SessionId: {}, UserId: {}, RoomId: {}", client.sessionId, userId, event.target)
    }

    @OnEvent(value = SocketIOEventName.LEAVE_ROOM)
    fun onLeaveRoom(client: SocketIOClient, event: SocketIORoomEvent, ackRequest: AckRequest){
        val userId = client.userId()
        client.leaveRoom(event.target)
        logger.info("user leave room, SessionId: {}, UserId: {}, RoomId: {}", client.sessionId, userId, event.target)
    }

    @OnEvent(value = SocketIOEventName.CHAT_MESSAGE)
    fun onChatMessage(client: SocketIOClient, event: SocketIOChatEvent, ackRequest: AckRequest){
        val userId = client.userId()
        event.timestamp = Instant.now().toString()
        val room = if(event.isLobby()){
            socketIOServer.broadcastOperations
        }else{
            socketIOServer.getRoomOperations(event.target)
        }
        room.sendEvent(SocketIOEventName.CHAT_MESSAGE, event)
        logger.info("user chat, SessionId: {}, UserId: {}, To: {}", client.sessionId, userId, event.target)
    }
    
    @OnEvent(value = SocketIOEventName.HEALTH_CHECK)
    fun onHealthCheck(client: SocketIOClient, ackRequest: AckRequest) {
        logger.info("Health check request received, SessionId: {}", client.sessionId)
        val response = SocketIOHealthCheckResponse()
        
        if (ackRequest.isAckRequested) {
            ackRequest.sendAckData(response)
            logger.info("Health check response sent, SessionId: {}", client.sessionId)
        } else {
            client.sendEvent(SocketIOEventName.HEALTH_CHECK, response)
            logger.info("Health check response sent via event, SessionId: {}", client.sessionId)
        }
    }


    private fun SocketIOClient.userId(): String?{
        return allRooms.firstOrNull { it.startsWith(USER_PREFIX) }
            ?.substringAfter(USER_PREFIX)
    }

    private fun String.toJwt(): Jwt? {
        return try {
            jwtDecoder.decode(this)
        } catch (e: Exception) {
            null
        }
    }
}
