package com.salazar.cheers.shared.data.mapper

import cheers.media.v1.Media

fun Media.toMedia(): com.salazar.cheers.core.model.Media {
    return com.salazar.cheers.core.model.Media.Image(
        id = id,
        url = url,
    )
}