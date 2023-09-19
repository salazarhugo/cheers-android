plugins {
    id("cheers.android.feature")
    id("cheers.android.library.compose")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.salazar.cheers.feature.friend_request"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":core:util"))
    implementation(project(":domain"))
    implementation(project(":data:friendship"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Google Ads
    implementation(libs.play.services.ads)

    // Jetpack Compose
    implementation(libs.androidx.compose.material)
    implementation("androidx.compose.runtime:runtime-livedata")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}