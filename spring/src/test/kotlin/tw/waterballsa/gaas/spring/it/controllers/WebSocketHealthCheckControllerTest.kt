package tw.waterballsa.gaas.spring.it.controllers

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.spring.configs.socketio.SocketIOServer
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest

class WebSocketHealthCheckControllerTest : AbstractSpringBootTest() {
    
    @Autowired
    private lateinit var socketIOServer: SocketIOServer
    
    @Test
    fun testWebSocketHealthCheckEndpoint() {
        mockMvc.perform(get("/websocket/health"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").exists())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.service").value("Lobby Platform WebSocket Service"))
            .andExpect(jsonPath("$.clients").exists())
            .andExpect(jsonPath("$.rooms").exists())
    }
}
