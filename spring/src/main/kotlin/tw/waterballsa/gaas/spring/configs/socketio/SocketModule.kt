//package tw.waterballsa.gaas.spring.configs.socketio
//
//import com.corundumstudio.socketio.AckRequest
//import com.corundumstudio.socketio.SocketIOClient
//import com.corundumstudio.socketio.SocketIOServer
//import com.corundumstudio.socketio.listener.ConnectListener
//import com.corundumstudio.socketio.listener.DataListener
//import com.corundumstudio.socketio.listener.DisconnectListener
//import org.springframework.stereotype.Component
//import tw.waterballsa.gaas.application.usecases.CreateRoomUsecase
//import tw.waterballsa.gaas.domain.Message
//import tw.waterballsa.gaas.domain.Room
//import tw.waterballsa.gaas.domain.WalkingSkeleton
//import tw.waterballsa.gaas.spring.repositories.SocketService
//import tw.waterballsa.gaas.spring.repositories.dao.WalkingSkeletonDAO
//import java.util.logging.Logger
//
//
//@Component
//class SocketModule (
//    private val server: SocketIOServer,
//    private val socketService: SocketService,
//    private val walkingSkeletonDAO: WalkingSkeletonDAO
//) {
//
//    val log = Logger.getLogger("SocketModule")
//    init {
//        server.addConnectListener(onConnected())
//        server.addDisconnectListener(onDisconnected())
//        server.addEventListener("send_message", Message::class.java, onChatReceived())
//        server.addEventListener("show_reply", WalkingSkeleton::class.java, findGameRegistrationById())
//
//    }
//
//    private fun onChatReceived(): DataListener<Message> {
//        return DataListener { senderClient, data, ackSender ->
//            log.info(data.toString())
//            socketService.sendMessage(data.room, "get_message", senderClient, data.message)
//        }
//    }
//
//
//    private fun findGameRegistrationById(): DataListener<WalkingSkeleton>? {
//        return return DataListener { senderClient, data, ackSender ->
//            socketService.walk(data.value, "get_message",senderClient)
//        }
//    }
//
//    private fun onConnected(): ConnectListener {
//        return ConnectListener { client ->
//            val msg = client.handshakeData.getSingleUrlParam("message")
//            client.joinRoom(msg)
//            log.info("Socket ID[${client.sessionId.toString()}] Connected to socket")
//        }
//    }
//
//    private fun onDisconnected(): DisconnectListener {
//        return DisconnectListener { client ->
//            log.info("Client[${client.sessionId.toString()}] - Disconnected from socket")
//        }
//    }
//}