plugins {
    id("cheers.android.feature")
    id("cheers.android.library.compose")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.salazar.cheers.feature.create_post"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data:post"))
    implementation(project(":data:account"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.compose.material)

    // Work Manager
    implementation(libs.androidx.work.runtime.ktx)

    // Map Box SDK
    implementation(libs.mapbox)
    implementation(libs.mapbox.sdk.services)
//    implementation(libs.mapbox.search.android)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}