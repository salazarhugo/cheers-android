plugins {
    alias(libs.plugins.cheers.android.application)
    alias(libs.plugins.cheers.android.application.compose)
    alias(libs.plugins.cheers.android.application.firebase)
    alias(libs.plugins.cheers.android.room)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = File("/home/hugo/.android/cheers_debug.keystore")
        }
    }
    namespace = "com.salazar.cheers"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.salazar.cheers"
        minSdk = 31
        targetSdk = 35
        versionCode = 79
        versionName = "1.0.0-079"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
            ndk {
                debugSymbolLevel = "FULL"
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("long", "VERSION_CODE", "${defaultConfig.versionCode}")
            buildConfigField("String", "VERSION_NAME", "\"${defaultConfig.versionName}\"")
        }
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            versionNameSuffix = "_dev_debug"
//            applicationIdSuffix = ".dev"
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("long", "VERSION_CODE", "${defaultConfig.versionCode}")
            buildConfigField("String", "VERSION_NAME", "\"${defaultConfig.versionName}\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    packaging.resources {
        excludes.add("/META-INF/AL2.0")
        excludes.add("/META-INF/LGPL2.1")
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(projects.core.protobuf)
    implementation(projects.core.ui)
    implementation(projects.core.util)
    implementation(projects.core.model)
    implementation(projects.core.shared)
    implementation(projects.core.db)
    implementation(projects.core.analytics)

    implementation(projects.feature.home)
    implementation(projects.feature.chat)
    implementation(projects.feature.map)
    implementation(projects.feature.search)
    implementation(projects.feature.profile)
    implementation(projects.feature.editProfile)
    implementation(projects.feature.signin)
    implementation(projects.feature.signup)
    implementation(projects.feature.notifications)
    implementation(projects.feature.settings)
    implementation(projects.feature.createPost)
    implementation(projects.feature.createNote)
    implementation(projects.feature.passcode)
    implementation(projects.feature.friendRequest)
    implementation(projects.feature.parties)
    implementation(projects.feature.ticket)
    implementation(projects.feature.friendList)
    implementation(projects.feature.comment)
    implementation(projects.feature.postLikes)
    implementation(projects.feature.premium)
    implementation(projects.feature.drink)

    implementation(projects.domain)

    implementation(projects.data.post)
    implementation(projects.data.user)
    implementation(projects.data.note)
    implementation(projects.data.party)
    implementation(projects.data.friendship)
    implementation(projects.data.activity)
    implementation(projects.data.story)
    implementation(projects.data.billing)
    implementation(projects.data.ticket)
    implementation(projects.data.comment)
    implementation(projects.data.drink)
    implementation(projects.data.remoteConfig)
    implementation(projects.data.search)
    implementation(projects.data.map)
    implementation(projects.data.chat)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.kotlinx.coroutines.play.services)

    // KSP
    implementation(libs.symbol.processing.api)

    // Klaxon
    implementation(libs.klaxon)

    // Accompanist
    implementation(libs.accompanist.navigation.material)
    implementation(libs.accompanist.navigation.animation)

    // Compose-State-Events
    implementation(libs.compose.state.events)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.navigation.compose)

    // Jetpack Compose BOM
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.runtime.runtime.livedata)

    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
//    implementation(libs.androidx.lifecycle.livedata.ktx)

    // OkHttp BOM
    implementation(platform(libs.okhttp3.bom))
    implementation(libs.okhttp3.okhttp)
    implementation(libs.okhttp3.logging.interceptor)

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
        exclude(group = "com.google.protobuf")
        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }

    // App Check Play Integrity
    implementation(libs.firebase.appcheck.playintegrity)

    // App Check Debug
    implementation(libs.firebase.appcheck.debug) {
        exclude(group = "com.google.protobuf")
    }

    // Firebase Authentication
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)

    //Firebase Dynamic-Links
    implementation(libs.firebase.dynamic.links.ktx)

    // Firebase Messaging
    implementation(libs.firebase.messaging.ktx) {
        exclude(group = "com.google.protobuf")
    }

    // Firebase Storage
    implementation(libs.firebase.storage.ktx) {
        exclude(group = "com.google.protobuf")
    }

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

    // Play In-App Update:
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    // <!------ Debugging ------!>
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

apply(plugin = "com.google.gms.google-services")