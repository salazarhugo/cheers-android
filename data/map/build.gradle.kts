plugins {
    alias(libs.plugins.cheers.android.library)
    alias(libs.plugins.cheers.android.hilt)
    alias(libs.plugins.cheers.android.room)
}

android {
    namespace = "com.salazar.cheers.data.map"
    compileSdk = 34

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}
dependencies {
    
    implementation(projects.core.model)
    implementation(projects.core.protobuf)
    implementation(projects.core.util)
    implementation(projects.core.shared)
    implementation(projects.core.db)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Map Box SDK
    implementation(libs.mapbox)
    implementation(libs.mapbox.search.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}