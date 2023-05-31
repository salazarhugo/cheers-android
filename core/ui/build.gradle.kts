plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin)
}

android {
    namespace = "com.salazar.cheers.core.ui"
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
}

dependencies {
    implementation(project(":core:util"))
    implementation(project(":core:model"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Jetpack Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

    // Accompanist
    implementation(libs.accompanist.navigation.material)
    implementation(libs.accompanist.navigation.animation)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    // Accompanist Permissions
    implementation(libs.accompanist.permissions)

    // Material design icons
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)
    implementation(project(mapOf("path" to ":core:protobuf")))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}