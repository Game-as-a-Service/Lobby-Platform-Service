package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.User

class UserUpdatedEvent(
    val id: User.Id,
    val email: String,
    val nickname: String,
) : DomainEvent()
