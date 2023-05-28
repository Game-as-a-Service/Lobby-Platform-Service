package tw.waterballsa.gaas.spring.configs.securities

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.servers.Server
import io.swagger.v3.oas.annotations.servers.ServerVariable
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
        info = Info(
                title = "Lobby Platform API",
                version = "1.0.0",
                description = "The API of the Lobby Platform"
        ),
        servers = [
            Server(url = "{schema}://localhost:{port}",
                    description = "Local Server",
                    variables = [
                        ServerVariable(
                                name = "port",
                                defaultValue = "8087",
                                description = "The port of the server"),
                        ServerVariable(
                                name = "schema",
                                defaultValue = "http",
                                description = "The schema of the server")
                    ]),
            Server(url = "https://api.gaas.waterballsa.tw",
                    description = "Prod Server")
        ]
)
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "Authentication",
        description = "JWT",
        scheme = "Bearer",
        bearerFormat = "JWT"
)
class OpenApiConfig {
}