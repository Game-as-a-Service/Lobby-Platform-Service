package tw.waterballsa.gaas.spring.controllers

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
class OAuth2Controller {

    @GetMapping("/")
    fun home(model: Model?, @AuthenticationPrincipal principal: OidcUser?): String {
        return principal?.idToken?.tokenValue ?: "index"
    }
}



