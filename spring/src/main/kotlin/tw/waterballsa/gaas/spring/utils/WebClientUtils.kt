package tw.waterballsa.gaas.spring.utils

import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponents

object WebClientUtils {

    inline fun <reified T> get(uri: UriComponents, type: Class<T>): T? = get(uri.toUriString(), type)

    inline fun <reified T> get(uriString: String, type: Class<T>): T? =
        WebClient.builder()
            .baseUrl(uriString)
            .build()
            .get()
            .retrieve()
            .bodyToMono(type)
            .block()

    inline fun <reified T> get(uri: UriComponents, type: ParameterizedTypeReference<T>): T? =
        get(uri.toUriString(), type)

    inline fun <reified T> get(uriString: String, type: ParameterizedTypeReference<T>): T? =
        WebClient.builder()
            .baseUrl(uriString)
            .build()
            .get()
            .retrieve()
            .bodyToMono(type)
            .block()
}
