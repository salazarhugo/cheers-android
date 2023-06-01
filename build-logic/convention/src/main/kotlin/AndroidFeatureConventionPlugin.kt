import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("cheers.android.library")
                apply("cheers.android.hilt")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("implementation", project(":common"))
                add("implementation", project(":core:model"))
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:util"))
                add("implementation", project(":core:protobuf"))
                add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
                add(
                    "implementation",
                    libs.findLibrary("androidx.lifecycle.lifecycle.runtime.ktx").get()
                )
                add(
                    "implementation",
                    libs.findLibrary("androidx.lifecycle.lifecycle.viewmodel.ktx").get()
                )
                add("implementation", libs.findLibrary("androidx.lifecycle.runtime.compose").get())

                add("testImplementation", libs.findLibrary("junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx.junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx.espresso.core").get())
            }
        }
    }
}