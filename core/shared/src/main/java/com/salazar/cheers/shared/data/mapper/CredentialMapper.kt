package com.salazar.cheers.shared.data.mapper

import cheers.auth.v1.Credential

fun Credential.toCredential(): com.salazar.cheers.core.model.Credential {
    return com.salazar.cheers.core.model.Credential(
        id = id.toString(),
        name = "",
        deviceName = device.name,
        lastUsed = lastUsed,
    )
}