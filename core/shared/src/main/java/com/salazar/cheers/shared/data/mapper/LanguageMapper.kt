package com.salazar.cheers.shared.data.mapper

import com.salazar.cheers.core.model.Language

fun com.salazar.cheers.Language.toDomain(): Language {
    return Language.entries.find { it.value == this.name } ?: Language.ENGLISH
}
