plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
    id("kotlin-kapt")
}

android {
    namespace = "com.salazar.cheers.feature.chat"
    compileSdk = 33

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
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
    packaging {
        resources.excludes.add("META-INF/*")
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(project(":common"))
    implementation(project(":core:protobuf"))
    implementation(project(":core:ui"))
    implementation(project(":core:util"))
    implementation(project(":core:model"))

    // OkHttp BOM
    implementation(platform("com.squareup.okhttp3:okhttp-bom:5.0.0-alpha.11"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Moshi
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
    kapt(libs.androidx.hilt.compiler)
    kapt(libs.hilt.android.compiler)

    // Jetpack Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.runtime:runtime-livedata")

    // Lifecycle
    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Material design icons
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // Coil for Compose
    implementation(libs.coil.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Work Manager
    implementation(libs.androidx.work.runtime.ktx)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
