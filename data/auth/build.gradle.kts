plugins {
    id("cheers.android.library")
    id("cheers.android.library.compose")
    id("cheers.android.hilt")
    id("cheers.android.room")
}

android {
    namespace = "com.salazar.cheers.data.auth"

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":auth"))

    implementation(project(":core:shared"))
    implementation(project(":core:model"))
    implementation(project(":core:protobuf"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Credential Manager
    implementation("androidx.credentials:credentials:1.2.0")
    // needed for credentials support from play services, for devices running
    // Android 13 and below.
    implementation("androidx.credentials:credentials-play-services-auth:1.2.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Googleid
    implementation(libs.googleid)

    // Google Sign In
    implementation(libs.play.services.auth)

    // Firebase
    implementation(platform(libs.firebase.bom)) {
        exclude(group = "com.google.protobuf")
    }
    implementation("com.google.firebase:firebase-auth-ktx")

    // Datastore
    implementation(libs.androidx.datastore.preferences)

    // Moshi
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}