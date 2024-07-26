plugins {
    alias(libs.plugins.cheers.android.library)
    alias(libs.plugins.cheers.android.hilt)
    alias(libs.plugins.cheers.android.room)
}

android {
    namespace = "com.salazar.cheers.data.remote_config"
    compileSdk = 34

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    
    implementation(projects.core.model)
    implementation(projects.domain)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Firebase
    implementation(libs.firebase.config)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}