package com.salazar.cheers.shared.data.mapper

import com.salazar.cheers.shared.data.response.FinishRegistrationResponse

fun FinishRegistrationResponse.toDomain(): String {
    return this.token!!
}

