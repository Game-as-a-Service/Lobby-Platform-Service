package tw.waterballsa.gaas.spring.controllers

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import tw.waterballsa.gaas.domain.Message
import java.text.SimpleDateFormat
import java.util.*


@Controller
class SocketController {

    @MessageMapping("/message")
    @SendTo("/topic/chat")
    fun handleMessage(message: String): String {
        // Process the message (you can add your custom logic here)
        return message
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    @Throws(Exception::class)
    fun send(message: Message): String? {
        val time = SimpleDateFormat("HH:mm").format(Date())
        return "OutputMessage(message.getFrom(), message.getText(), time)"
    }
}