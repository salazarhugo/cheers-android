plugins {
    alias(libs.plugins.cheers.android.library)
    alias(libs.plugins.cheers.android.library.compose)
    alias(libs.plugins.cheers.android.hilt)
}

android {
    namespace = "com.salazar.cheers.core.ui"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(projects.common)
    implementation(projects.core.util)
    implementation(projects.core.model)
    implementation(projects.core.shared)

    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")

    implementation(projects.data.party)
    implementation(projects.data.post)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // ExoPlayer 3
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")

    // Jetpack Compose
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

    // QR Code
    implementation(libs.composed.barcodes)

    // Amplituda
    implementation("com.github.lincollincol:compose-audiowaveform:1.1.1")
    implementation("com.github.lincollincol:amplituda:2.2.2")

    // Accompanist
    implementation(libs.accompanist.navigation.material)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.pager.indicators)

    // Map Box SDK
    implementation(libs.mapbox)
    implementation("com.mapbox.extension:maps-compose:11.0.0")
    implementation(libs.mapbox.sdk.services)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    // Accompanist Permissions
    implementation(libs.accompanist.permissions)

    implementation(project(mapOf("path" to ":core:protobuf")))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}