plugins {
    id("cheers.android.library")
    id("cheers.android.library.compose")
}

android {
    namespace = "com.salazar.cheers.core.util"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Jetpack Compose
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

    // Firebase BOM
    implementation(platform(libs.firebase.bom)) {
        exclude(group = "com.google.protobuf")
    }
    implementation("com.google.firebase:firebase-auth-ktx") {
        exclude(group = "com.google.protobuf")
    }
    implementation("com.google.firebase:firebase-dynamic-links-ktx") {
        exclude(group = "com.google.protobuf")
    }
    implementation("com.google.firebase:firebase-storage-ktx") {
        exclude(group = "com.google.protobuf")
    }

    // Snapchat
    implementation(libs.creativekit)
    implementation(libs.loginkit)

    // Camera X
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}