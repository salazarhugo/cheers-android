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

    implementation(project(":common"))
    implementation(project(":core:model"))

    implementation(project(":data:user"))
    implementation(project(":data:post"))
    implementation(project(":data:note"))
    implementation(project(":data:friendship"))
}