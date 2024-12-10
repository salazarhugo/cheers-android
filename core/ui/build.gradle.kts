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
    
    implementation(projects.core.util)
    implementation(projects.core.model)
    implementation(projects.core.shared)
    implementation(libs.androidx.compose.runtime.runtime.livedata)

    debugImplementation(libs.androidx.ui.tooling)

    implementation(projects.data.party)
    implementation(projects.data.post)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Work Manager
    implementation(libs.androidx.work.runtime.ktx)

    // ExoPlayer 3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)

    // Jetpack Compose
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

    // QR Code
    implementation(libs.composed.barcodes)

    // Amplituda
    implementation(libs.compose.audiowaveform)
    implementation(libs.amplituda)

    // Accompanist
    implementation(libs.accompanist.navigation.material)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    // Map Box SDK
    implementation(libs.mapbox)
    implementation(libs.mapbox.compose)
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