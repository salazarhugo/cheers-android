plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.salazar.cheers.protobuf"
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
//    sourceSets.getByName("main") {
//        proto {
//            srcDir("src/main/proto")
//        }
//    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.14.0"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.52.1"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
            task.plugins {
                create("grpc") {
                    option("lite")
                }
                create("grpckt") {
                }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    api(libs.grpc.okhttp)
    api(libs.grpc.protobuf.lite) {
        exclude(group = "com.google.firebase", module = "protolite-well-known-types")
    }
    api(libs.grpc.stub)
    api(libs.grpc.kotlin.stub)
    compileOnly(libs.annotations.api)
//    implementation("com.google.protobuf:protobuf-javalite:3.21.12")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}