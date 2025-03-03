package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.slf4j.Logger
import org.springframework.security.oauth2.jwt.JwtDecoder
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.spring.configs.socketio.event.SocketIOHealthCheckResponse

@ExtendWith(MockitoExtension::class)
class SocketIOEventHandlerTest {

    @Mock
    private lateinit var socketIOServer: SocketIOServer

    @Mock
    private lateinit var jwtDecoder: JwtDecoder

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var client: SocketIOClient

    @Mock
    private lateinit var ackRequest: AckRequest

    private lateinit var handler: SocketIOEventHandler

    @BeforeEach
    fun setUp() {
        handler = spy(SocketIOEventHandler(socketIOServer, jwtDecoder, userRepository))
        // Set the logger of the handler to mock to avoid log recording interference during testing.
        val loggerField = SocketIOEventHandler::class.java.getDeclaredField("logger")
        loggerField.isAccessible = true
        loggerField.set(handler, mock(Logger::class.java))
    }

    @Test
    fun `should respond with health check data when ack is requested`() {
        // Given
        `when`(ackRequest.isAckRequested).thenReturn(true)
        `when`(client.sessionId).thenReturn("test-session-id")

        // When
        handler.onHealthCheck(client, ackRequest)

        // Then
        val responseCaptor = ArgumentCaptor.forClass(SocketIOHealthCheckResponse::class.java)
        verify(ackRequest).sendAckData(responseCaptor.capture())

        val response = responseCaptor.value
        assert(response.status == "UP")
        assert(response.service == "Lobby Platform WebSocket Service")
        assert(response.timestamp.isNotBlank())
    }

    @Test
    fun `should send event with health check data when ack is not requested`() {
        // Given
        `when`(ackRequest.isAckRequested).thenReturn(false)
        `when`(client.sessionId).thenReturn("test-session-id")

        // When
        handler.onHealthCheck(client, ackRequest)

        // Then
        val eventNameCaptor = ArgumentCaptor.forClass(String::class.java)
        val responseCaptor = ArgumentCaptor.forClass(SocketIOHealthCheckResponse::class.java)

        verify(client).sendEvent(eventNameCaptor.capture(), responseCaptor.capture())
        
        assert(eventNameCaptor.value == SocketIOEventName.HEALTH_CHECK)
        val response = responseCaptor.value
        assert(response.status == "UP")
        assert(response.service == "Lobby Platform WebSocket Service")
        assert(response.timestamp.isNotBlank())
    }
}
