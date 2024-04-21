package com.salazar.cheers.core.model

data class UserPreference(
    val id: String,
    val theme: Theme
)

enum class Theme(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System default")
}
