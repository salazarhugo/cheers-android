package com.salazar.cheers.data.account.mapper

import com.salazar.cheers.data.account.Account
import com.salazar.cheers.shared.data.response.LoginResponse

fun LoginResponse.toAccount(): Account {
    user.apply {
        return Account(
            id = id.orEmpty(),
            name = name.orEmpty(),
            username = username.orEmpty(),
            email = email.orEmpty(),
            picture = picture.orEmpty(),
            banner = banner.orEmpty(),
        )
    }
}