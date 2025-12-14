package com.r8booster

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

class KotlinDaemonKillerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val killTask = project.tasks.register(
            "killKotlinCompileDaemon",
            KillKotlinCompileDaemonTask::class.java
        ) { task ->
            task.kotlinDaemonKillInfo.set(
                project.providers.of(KillKotlinCompileDaemonValueSource::class.java) {
                    it.parameters.commands.set(KillKotlinCompileDaemonValueSource.DEFAULT_COMMAND)
                }
            )
        }

        registerAndroidPluginHooks(project, killTask)

        // Fallback for builds that do not apply Android plugins but still expose R8-like tasks
        project.tasks.matching { it.name.contains("R8", ignoreCase = true) }.configureEach { task ->
            attachKillTask(task, killTask)
        }
    }

    private fun registerAndroidPluginHooks(
        project: Project,
        killTask: TaskProvider<KillKotlinCompileDaemonTask>
    ) {
        val androidPlugins = listOf(
            "com.android.application",
            "com.android.library",
            "com.android.test",
            "com.android.dynamic-feature"
        )

        androidPlugins.forEach { pluginId ->
            project.pluginManager.withPlugin(pluginId) {
                wireKillTaskBeforeR8(project, killTask)
            }
        }
    }

    private fun wireKillTaskBeforeR8(
        project: Project,
        killTask: TaskProvider<KillKotlinCompileDaemonTask>
    ) {
        val r8TaskClass = loadR8TaskClass()
        if (r8TaskClass != null) {
            project.tasks.withType(r8TaskClass).configureEach { task ->
                attachKillTask(task, killTask)
            }
        } else {
            project.tasks.matching { it.name.contains("R8", ignoreCase = true) }.configureEach { task ->
                attachKillTask(task, killTask)
            }
        }
    }

    private fun attachKillTask(task: Task, killTask: TaskProvider<out Task>) {
        task.dependsOn(killTask)
        task.mustRunAfter(killTask)
    }

    private fun loadR8TaskClass(): Class<out Task>? =
        runCatching { Class.forName(R8_TASK_CLASS_NAME).asSubclass(Task::class.java) }.getOrNull()

    companion object {
        private const val R8_TASK_CLASS_NAME = "com.android.build.gradle.internal.tasks.R8Task"
    }
}
