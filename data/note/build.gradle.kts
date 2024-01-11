plugins {
    id("cheers.android.library")
    id("cheers.android.hilt")
    id("cheers.android.room")
}

android {
    namespace = "com.salazar.cheers.data.note"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(project(":common"))
    implementation(project(":core:model"))
    implementation(project(":core:protobuf"))

    // Firebase
    implementation(platform(libs.firebase.bom)) {
        exclude(group = "com.google.protobuf")
    }

    implementation("com.google.firebase:firebase-auth-ktx")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}