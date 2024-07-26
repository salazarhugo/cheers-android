plugins {
    alias(libs.plugins.cheers.android.feature)
    alias(libs.plugins.cheers.android.library.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.salazar.cheers.feature.signup"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(projects.data.user)
    api(projects.data.auth)
    
    implementation(projects.domain)
    implementation(libs.material)

    implementation("com.google.android.gms:play-services-tasks:18.0.2")

    // Jetpack Compose
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

    // Compose-State-Events
    implementation(libs.compose.state.events)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}