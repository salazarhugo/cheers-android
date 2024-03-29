package com.salazar.cheers.data.account.mapper

import com.salazar.cheers.data.account.Account
import com.salazar.cheers.shared.data.response.LoginResponse

fun LoginResponse.toAccount(idToken: String = String()): Account {
    user.apply {
        return Account(
            id = id.orEmpty(),
            name = name.orEmpty(),
            username = username.orEmpty(),
            verified = verified ?: false,
            email = email.orEmpty(),
            picture = picture.orEmpty(),
            banner = banner.orEmpty(),
            idToken = idToken,
        )
    }
}