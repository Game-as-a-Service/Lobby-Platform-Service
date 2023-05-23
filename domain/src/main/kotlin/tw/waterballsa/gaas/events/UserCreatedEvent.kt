package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.User

data class UserCreatedEvent(
    val id: User.Id,
    val email: String,
    val nickname: String = "",
) : DomainEvent()
