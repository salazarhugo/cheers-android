buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.gradle)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
}