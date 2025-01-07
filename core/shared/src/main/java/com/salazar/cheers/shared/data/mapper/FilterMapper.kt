package com.salazar.cheers.shared.data.mapper

import cheers.type.FilterOuterClass
import com.salazar.cheers.core.model.Filter

fun Filter.toFilterPb(): FilterOuterClass.Filter {
    return FilterOuterClass.Filter.newBuilder()
        .setId(id)
        .setName(name)
        .setValue(value)
        .setIsActive(selected)
        .build()
}

fun FilterOuterClass.Filter.toFilter(): Filter {
    return Filter(
        id = id,
        name = name,
        value = value,
        selected = isActive
    )
}
