plugins {
    alias(libs.plugins.cheers.android.feature)
    alias(libs.plugins.cheers.android.library.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.salazar.cheers.feature.map"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(projects.domain)
    implementation(projects.data.user)
    implementation(projects.data.post)
    implementation(projects.data.map)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Jetpack Compose
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

    // Accompanist System UI Controller
    implementation(libs.accompanist.systemuicontroller)

    // Kotlinx Coroutines Play Services
    implementation(libs.kotlinx.coroutines.play.services)

    // Accompanist
    implementation(libs.accompanist.navigation.material)
    implementation(libs.accompanist.navigation.animation)

    // Map Box SDK
    implementation(libs.mapbox)
    implementation(libs.mapbox.compose)
//    implementation(libs.mapbox.sdk.services)
    implementation(libs.mapbox.search.android)
}