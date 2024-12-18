plugins {
    alias(libs.plugins.cheers.android.library)
    alias(libs.plugins.cheers.android.library.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.salazar.cheers.core.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}