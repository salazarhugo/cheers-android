plugins {
    id("cheers.android.application")
    id("cheers.android.application.compose")
    id("cheers.android.application.firebase")
    id("cheers.android.room")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.salazar.cheers"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.salazar.cheers"
        minSdk = 28
        targetSdk = 34
        versionCode = 64
        versionName = "1.0.0-064"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(project(":ads"))
    implementation(project(":common"))

    implementation(project(":core:protobuf"))
    implementation(project(":core:ui"))
    implementation(project(":core:util"))
    implementation(project(":core:model"))

    implementation(project(":feature:home"))
    implementation(project(":feature:chat"))
    implementation(project(":feature:map"))
    implementation(project(":feature:search"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:edit_profile"))
    implementation(project(":feature:signin"))
    implementation(project(":feature:notifications"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:create_post"))
    implementation(project(":feature:create_note"))

    implementation(project(":domain"))

    implementation(project(":data:post"))
    implementation(project(":data:user"))
    implementation(project(":data:note"))
    implementation(project(":data:party"))
    implementation(project(":data:friendship"))
    implementation(project(":data:activity"))
    implementation(project(":data:story"))
    implementation(project(":data:billing"))


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.grpc.okhttp)
    implementation(libs.grpc.protobuf.lite) {
        exclude(group = "com.google.firebase", module = "protolite-well-known-types")
    }
    implementation(libs.grpc.stub)
    implementation(libs.grpc.kotlin.stub)
    compileOnly(libs.annotations.api) // necessary for Java 9+

//    implementation("com.google.protobuf:protobuf-javalite:3.21.12")

//    implementation("com.google.protobuf:protobuf-javalite:3.21.7"

    // KSP
    implementation(libs.symbol.processing.api)

    // Klaxon
    implementation(libs.klaxon)

    // Accompanist
    implementation(libs.accompanist.navigation.material)
    implementation(libs.accompanist.navigation.animation)

    // Compose-State-Events
    implementation(libs.compose.state.events)

    // QR Code
    implementation(libs.composed.barcodes)

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))

    // Jetpack Compose BOM
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.runtime:runtime-livedata")

    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
//    implementation(libs.androidx.lifecycle.livedata.ktx)

    // OkHttp BOM
    implementation(platform("com.squareup.okhttp3:okhttp-bom:5.0.0-alpha.11"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    // Moshi
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

    // Accompanist System UI Controller
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.placeholder.material)

    // Accompanist Pager
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    // Accompanist Permissions
    implementation(libs.accompanist.permissions)

    // Camera X
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)

    // Coil for Compose
    implementation(libs.coil.compose)

    // Coil SVG
    implementation(libs.coil.svg)

    // Google Ads
    implementation(libs.play.services.ads)

    // Google Billing
    implementation(libs.billing.ktx)

    // Constraint layout
    implementation(libs.androidx.constraintlayout)

    // Firebase BOM
    implementation(platform(libs.firebase.bom)) {
//        exclude(group = "com.google.protobuf")
//        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
//        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }

    // App Check Play Integrity
    implementation("com.google.firebase:firebase-appcheck-playintegrity")

    // App Check Debug
    implementation(libs.firebase.appcheck.debug)

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation(libs.play.services.auth)

    //Firebase Dynamic-Links
    implementation("com.google.firebase:firebase-dynamic-links-ktx")

    // Firebase Firestore
//    implementation("com.google.firebase:firebase-firestore-ktx"

    // Firebase Functions
    implementation("com.google.firebase:firebase-functions-ktx")

    // Firebase Messaging
    implementation("com.google.firebase:firebase-messaging-ktx")

    // Firebase Storage
    implementation("com.google.firebase:firebase-storage-ktx")

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.androidx.hilt.compiler)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work.multiprocess)

    implementation(libs.play.services.maps)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // optional - Paging 3 Integration
    implementation(libs.androidx.room.paging)

    // Paging
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    // Work Manager
    implementation(libs.androidx.work.runtime.ktx)

    // Snapchat
    implementation(libs.creativekit)
    implementation(libs.loginkit)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Swipe Refresh
    implementation(libs.accompanist.swiperefresh)

    // User Messaging Platform
    implementation(libs.user.messaging.platform)
    implementation(libs.androidx.animation.graphics)

    // Map Box SDK
    implementation(libs.android)
    implementation(libs.mapbox.sdk.services)
    implementation(libs.mapbox.search.android)

    // <!------ Debugging ------!>
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
}

apply(plugin = "com.google.gms.google-services")