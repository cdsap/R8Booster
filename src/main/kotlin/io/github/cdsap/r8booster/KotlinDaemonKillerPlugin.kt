package io.github.cdsap.r8booster

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskContainer
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
        // Defer compile-task wiring until the Android application plugin is present.
        pluginManager.withPlugin("com.android.application") {
            wireKillTaskDependencies(tasks, killTask)
        }
    }

    private fun wireKillTaskDependencies(
        tasks: TaskContainer,
        killTask: TaskProvider<KillKotlinCompileDaemonTask>
    ) {
        tasks.withType<KotlinCompilationTask<*>>().configureEach {
            finalizedBy(killTask)
        }

        tasks.withType<JavaCompile>().configureEach {
            dependsOn(killTask)
        }
    }
}
