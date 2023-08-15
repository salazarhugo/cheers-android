plugins {
    id("cheers.android.library")
    id("cheers.android.hilt")
    id("cheers.android.room")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.salazar.cheers.data.account"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(project(":common"))
    implementation(project(":core:model"))
    implementation(project(":core:shared"))

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}