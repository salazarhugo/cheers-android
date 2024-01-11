plugins {
    id("cheers.android.library")
    id("cheers.android.hilt")
    id("cheers.android.room")
}

android {
    namespace = "com.salazar.cheers.domain"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(project(":auth"))
    implementation(project(":common"))
    implementation(project(":core:model"))
    implementation(project(":core:shared"))

    implementation(project(":data:user"))
    api(project(":data:account"))
    implementation(project(":data:auth"))
    implementation(project(":data:post"))
    implementation(project(":data:party"))
    implementation(project(":data:note"))
    implementation(project(":data:activity"))
    implementation(project(":data:story"))
    implementation(project(":data:friendship"))
    implementation(project(":data:drink"))
    implementation(project(":data:ticket"))
    implementation(project(":data:comment"))

    // Firebase BOM
    implementation(platform(libs.firebase.bom)) {
        exclude(group = "com.google.protobuf")
    }
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation(libs.play.services.auth)

    implementation(libs.androidx.credentials)
    implementation(libs.googleid)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}