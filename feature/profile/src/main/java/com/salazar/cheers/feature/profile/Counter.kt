package com.salazar.cheers.feature.profile

import androidx.annotation.PluralsRes

data class Counter(
    @PluralsRes
    val name: Int,
    val value: Int,
    val navId: Int? = null,
)
