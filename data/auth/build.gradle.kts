plugins {
    alias(libs.plugins.cheers.android.library)
    alias(libs.plugins.cheers.android.library.compose)
    alias(libs.plugins.cheers.android.hilt)
    alias(libs.plugins.cheers.android.room)
}

android {
    namespace = "com.salazar.cheers.data.auth"

    signingConfigs {
        getByName("debug") {
            storeFile = File("/home/hugo/.android/cheers_debug.keystore")
        }
    }
    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
    }
}

dependencies {
    implementation(projects.core.shared)
    implementation(projects.core.model)
    implementation(projects.core.protobuf)
    implementation(projects.core.db)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Credential Manager
    implementation(libs.androidx.credentials)
    // needed for credentials support from play services, for devices running
    // Android 13 and below.
    implementation(libs.androidx.credentials.play.services.auth)

    // Gson
    implementation(libs.gson)

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