plugins {
    alias(libs.plugins.cheers.android.feature)
    alias(libs.plugins.cheers.android.library.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.salazar.cheers.feature.settings"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    
    implementation(projects.core.ui)
    implementation(projects.domain)
    implementation(projects.data.user)
    implementation(projects.data.auth)
    implementation(projects.data.billing)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Jetpack Compose
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

    // Google Billing
    implementation(libs.billing.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}