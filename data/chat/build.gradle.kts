plugins {
    alias(libs.plugins.cheers.android.library)
    alias(libs.plugins.cheers.android.hilt)
    alias(libs.plugins.cheers.android.room)
}

android {
    namespace = "com.salazar.cheers.data.chat"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.util)
    implementation(projects.core.protobuf)
    implementation(projects.core.shared)
    implementation(projects.data.account)
    implementation(projects.data.post)
    implementation(projects.core.db)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // OkHttp BOM
    implementation(platform(libs.okhttp3.bom))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation(libs.retrofit.converter.gson)

    // Moshi
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

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