package com.salazar.cheers.shared.data.mapper

import cheers.user.v1.CheckUsernameResponse
import com.salazar.cheers.core.model.CheckUsernameResult

fun CheckUsernameResponse.toCheckUsernameResult(): CheckUsernameResult {
    return CheckUsernameResult(
        valid = valid,
        invalidReason = invalidReason.toString(),
    )
}