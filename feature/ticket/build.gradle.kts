plugins {
    id("cheers.android.feature")
    id("cheers.android.library.compose")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.salazar.cheers.feature.ticket"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":domain"))
    implementation(project(":core:util"))
    implementation(project(":core:shared"))
    implementation(project(":data:ticket"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // QR Code
    implementation(libs.composed.barcodes)

    // Jetpack Compose
    implementation(libs.androidx.compose.material)
    implementation("androidx.compose.runtime:runtime-livedata")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
