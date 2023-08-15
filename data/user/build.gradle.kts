plugins {
    id("cheers.android.library")
    id("cheers.android.hilt")
    id("cheers.android.room")
}

android {
    namespace = "com.salazar.cheers.data.user"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(project(":common"))
    implementation(project(":core:model"))
    implementation(project(":core:protobuf"))
    implementation(project(":core:util"))

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-auth-ktx")

    // Datastore
    implementation(libs.androidx.datastore.preferences)

    // Work Manager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work.multiprocess)
    kapt(libs.androidx.hilt.compiler)
    kapt(libs.hilt.android.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}