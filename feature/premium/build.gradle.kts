plugins {
    alias(libs.plugins.cheers.android.feature)
    alias(libs.plugins.cheers.android.library.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.salazar.cheers.feature.premium"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(projects.domain)
    implementation(projects.data.billing)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}