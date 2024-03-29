package com.salazar.cheers.data.account

import com.salazar.cheers.shared.data.response.UserResponse

data class Account(
    val id: String = String(),
    val name: String = String(),
    val picture: String = String(),
    val banner: String = String(),
    val username: String = String(),
    val verified: Boolean = false,
    val email: String = String(),
    val idToken: String = String(),
)

fun UserResponse.toAccount(): Account {
    return Account(
        id = id,
        name = name.orEmpty(),
        picture = picture.orEmpty(),
        banner = banner.orEmpty(),
        username = username,
        verified = verified ?: false,
        email = email.orEmpty(),
    )
}
