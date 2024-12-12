plugins {
    alias(libs.plugins.cheers.android.feature)
    alias(libs.plugins.cheers.android.library.compose)
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
    
    implementation(projects.core.util)
    implementation(projects.domain)
    implementation(projects.data.friendship)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Google Ads
    implementation(libs.play.services.ads)

    // Jetpack Compose
    implementation(libs.androidx.compose.runtime.runtime.livedata)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}