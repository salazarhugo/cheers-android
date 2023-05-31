plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
//    id("com.google.protobuf")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.firebase-perf")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.salazar.cheers"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.salazar.cheers"
        minSdk = 28
        targetSdk = 33
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
        compilerOptions {
            freeCompilerArgs.addAll(
                listOf(
                    "-opt-in=kotlin.ExperimentalUnsignedTypes",
                    "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-opt-in=kotlinx.coroutines.InternalCoroutinesApi",
                    "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                    "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
                    "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                    "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                    "-opt-in=androidx.compose.runtime.ExperimentalComposeApi",
                    "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                    "-opt-in=coil.annotation.ExperimentalCoilApi",
                    "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                    "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi",
                    "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi",
                    "-opt-in=com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi",
                    "-opt-in=androidx.paging.ExperimentalPagingApi",
                    "-Xjvm-default=enable"
                )
            )
        }
    }
}

dependencies {
    implementation(project(":ads"))
    implementation(project(":common"))
    implementation(project(":feature:chat"))
    implementation(project(":core:protobuf"))
    implementation(project(":core:ui"))
    implementation(project(":core:util"))
    implementation(project(":core:model"))

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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.runtime:runtime-livedata")

    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.extensions)

    // OkHttp BOM
    implementation(platform("com.squareup.okhttp3:okhttp-bom:5.0.0-alpha.11"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    // Moshi
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

    // Material design icons
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

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

    // ExoPlayer 2
    implementation(libs.exoplayer)

    // Datastore
    implementation(libs.androidx.datastore.preferences)

    // Firebase BOM
    implementation(platform(libs.firebase.bom)) {
//        exclude(group = "com.google.protobuf")
//        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
//        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics-ktx")

    // App Check Play Integrity
    implementation("com.google.firebase:firebase-appcheck-playintegrity")

    // App Check Debug
    implementation(libs.firebase.appcheck.debug)

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation(libs.play.services.auth)

    // Firebase Crashlytics
    implementation("com.google.firebase:firebase-crashlytics-ktx")

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

    // Firebase Performance
    implementation("com.google.firebase:firebase-perf-ktx")

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
    kapt(libs.androidx.hilt.compiler)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work.multiprocess)

    implementation(libs.play.services.maps)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    // optional - Paging 3 Integration
    implementation(libs.androidx.room.paging)

    // Map Box SDK
    implementation("com.mapbox.maps:android:10.12.1") {
//        exclude group: "com.mapbox.android.core.location.LocationEngine"
    }
    implementation(libs.mapbox.sdk.services)
    implementation(libs.mapbox.search.android)

    // Paging
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    // Google"s Fused Location Provider
    implementation(libs.play.services.location)

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

    // <!------ Debugging ------!>
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
}

apply(plugin = "com.google.gms.google-services")