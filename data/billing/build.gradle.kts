plugins {
    id("cheers.android.library")
    id("cheers.android.hilt")
    id("cheers.android.room")
}

android {
    namespace = "com.salazar.cheers.data.billing"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":core:model"))
    implementation(project(":core:protobuf"))
    implementation(project(":core:util"))

    // Google Billing
    implementation(libs.billing.ktx)

    // Moshi
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}