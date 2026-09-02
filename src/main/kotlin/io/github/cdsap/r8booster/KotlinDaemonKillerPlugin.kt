package io.github.cdsap.r8booster

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class KotlinDaemonKillerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            val killTask = project.tasks.register(
                "killKotlinCompileDaemon",
                KillKotlinCompileDaemonTask::class.java
            )
            killTask.configure {
                kotlinDaemonKillInfo.set(project.providers.of(KillKotlinCompileDaemonValueSource::class.java) {
                    parameters.commands.set(KillKotlinCompileDaemonValueSource.DEFAULT_COMMAND)
                })
            }

            // Use withPlugin(id) so AGP classes are only loaded when the Android
            // application plugin is present (required for TestKit / non-AGP applies).
            pluginManager.withPlugin("com.android.application") {
                val androidComponents =
                    extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
                androidComponents.onVariants(androidComponents.selector().withBuildType("release")) {
                    tasks.withType<KotlinCompile>().configureEach {
                        finalizedBy(killTask)
                    }

                    tasks.withType<JavaCompile>().configureEach {
                        dependsOn(tasks.named<KillKotlinCompileDaemonTask>("killKotlinCompileDaemon"))
                    }
                }
            }
        }
    }
}
