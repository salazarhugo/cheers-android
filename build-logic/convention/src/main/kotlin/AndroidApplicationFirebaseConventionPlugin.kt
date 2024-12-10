import com.salazar.cheers.build_logic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
//                apply("com.google.gms.google-services")
//                apply("com.google.firebase.firebase-perf")
                apply("com.google.firebase.crashlytics")
            }


            dependencies {
                val bom = libs.findLibrary("firebase-bom").get()
                add("implementation", platform(bom))
                "implementation"(libs.findLibrary("firebase.analytics").get())
//                "implementation"(libs.findLibrary("firebase.performance").get()) {
//                    exclude(group = "com.google.protobuf")
//                }
                "implementation"(libs.findLibrary("firebase.crashlytics").get())
            }
        }
    }
}