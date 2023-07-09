package tw.waterballsa.gaas.spring.controllers.viewmodel

import tw.waterballsa.gaas.domain.User

data class UpdateUserViewModel(
    val id: User.Id,
    val email: String,
    val nickname: String
)