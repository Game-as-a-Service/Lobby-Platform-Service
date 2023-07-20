package tw.waterballsa.gaas.spring.configs

import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class DataHandler : TextWebSocketHandler() {


//    override fun handleMessage(session: WebSocketSession, webSocketMessage: WebSocketMessage<*>) {
//        session.sendMessage(TextMessage("Hello websocket"))
//    }

    @Throws(Exception::class)
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        // Handle incoming messages here
        val payload = message.payload
        // Process the payload as needed
       // "key": "user"

        // handshake 前 身份驗證

        // 大廳公開聊天

        // 1 對 1

        // 房間聊天


        session.sendMessage(TextMessage("Received: $payload"))
    }
}