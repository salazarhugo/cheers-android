plugins {
    alias(libs.plugins.cheers.android.library)
    alias(libs.plugins.cheers.android.hilt)
    alias(libs.plugins.cheers.android.room)
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

    implementation(projects.core.model)
    implementation(projects.core.shared)
    implementation(projects.core.db)

    implementation(projects.data.user)
    api(projects.data.account)
    implementation(projects.data.auth)
    implementation(projects.data.post)
    implementation(projects.data.party)
    implementation(projects.data.note)
    implementation(projects.data.activity)
    implementation(projects.data.story)
    implementation(projects.data.friendship)
    implementation(projects.data.drink)
    implementation(projects.data.ticket)
    implementation(projects.data.comment)
    implementation(projects.data.map)
    implementation(projects.data.chat)
    implementation(projects.data.billing)

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