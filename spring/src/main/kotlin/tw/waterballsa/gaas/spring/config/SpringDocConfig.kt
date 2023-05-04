package tw.waterballsa.gaas.spring.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringDocConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .addSecurityItem(SecurityRequirement().addList("Authorization"))
            .info(getInfo())
    }

    private fun getInfo(): Info {
        return Info().title("Lobby Platform API")
            .description("The API documentation for Game As A Service")
            .version("1.0.0")
            .license(getLicense())
    }

    private fun getLicense(): License {
        return License().name("Apache-2.0 license")
            .url("https://www.apache.org/licenses/LICENSE-2.0")
    }
}