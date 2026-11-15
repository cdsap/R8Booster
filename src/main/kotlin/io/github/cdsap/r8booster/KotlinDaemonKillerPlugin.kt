package io.github.cdsap.r8booster

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

class KotlinDaemonKillerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val killTask = project.registerKillTask()
        project.wireKillTaskIntoAndroidApplicationTasks(killTask)
    }

    private fun Project.registerKillTask(): TaskProvider<KillKotlinCompileDaemonTask> =
        tasks.register(
            "killKotlinCompileDaemon",
            KillKotlinCompileDaemonTask::class.java
        ) {
            commandLine.set(KotlinDaemonKillCommand.commandLine)
        }

    private fun Project.wireKillTaskIntoAndroidApplicationTasks(
        killTask: TaskProvider<KillKotlinCompileDaemonTask>
    ) {
        // Use withPlugin(id) so AGP classes are only loaded when the Android
        // application plugin is present (required for TestKit / non-AGP applies).
        pluginManager.withPlugin("com.android.application") {
            val androidComponents =
                extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
            androidComponents.onVariants(androidComponents.selector().withBuildType("release")) {
                tasks.withType<KotlinCompilationTask<*>>().configureEach {
                    finalizedBy(killTask)
                }

                tasks.withType<JavaCompile>().configureEach {
                    dependsOn(killTask)
                }
            }
        }
    }
}
