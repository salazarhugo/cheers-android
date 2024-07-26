plugins {
    alias(libs.plugins.cheers.android.library)
    alias(libs.plugins.cheers.android.library.compose)
    alias(libs.plugins.cheers.android.hilt)
}

android {
    namespace = "com.salazar.cheers.core.analytics"
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
}