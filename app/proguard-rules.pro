# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable, AnnotationDefault, *Annotation*

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
-keep class com.salazar.cheers.** { *; }
-keep class com.salazar.cheers.backend.** { *; }
-keep class com.salazar.cheers.data.** { *; }
-keep class com.salazar.cheers.internal.** { *; }

# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class com.google.googlesignin.** { *; }
-keepnames class com.google.googlesignin.* { *; }

-keep class com.google.android.gms.auth.** { *; }
-keep class kotlinx.coroutines.android.AndroidExceptionPreHandler
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory

-keepclassmembers class * extends androidx.work.Worker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }


# OhHttp 3
-dontwarn javax.annotation.**

# Mapbox
-keep class com.mapbox.android.telemetry.**
-keep class com.mapbox.android.core.location.**
-keep class android.arch.lifecycle.** { *; }
-keep class com.mapbox.android.core.location.** { *; }
-dontnote com.mapbox.mapboxsdk.**
-dontnote com.mapbox.android.gestures.**
-dontnote com.mapbox.mapboxsdk.plugins.**

-dontwarn okhttp3.internal.platform.** -dontwarn org.conscrypt.** -dontwarn org.bouncycastle.** -dontwarn org.openjsse.**
-dontwarn com.google.protobuf.java_com_google_android_gmscore_sdk_target_granule__proguard_group_gtm_N1281923064GeneratedExtensionRegistryLite**
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep class com.tobrun.datacompat.annotation.**

-dontwarn javax.naming.NamingEnumeration
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.Attribute
-dontwarn javax.naming.directory.Attributes
-dontwarn javax.naming.directory.DirContext
-dontwarn javax.naming.directory.InitialDirContext