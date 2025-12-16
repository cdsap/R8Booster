package io.github.cdsap.r8booster

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.tasks.R8Task
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinDaemonKillerPlugin : Plugin<Project> {
    override fun apply(project: Project) {


        val killTask = project.tasks.register(
            "killKotlinCompileDaemon",
            KillKotlinCompileDaemonTask::class.java
        )
        killTask.configure {
            kotlinDaemonKillInfo.set(project.providers.of(KillKotlinCompileDaemonValueSource::class.java) {
                parameters.commands.set(KillKotlinCompileDaemonValueSource.DEFAULT_COMMAND)
            })
        }
        with(project) {

            val androidComponents =
                extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
            plugins.withType(AppPlugin::class.java) {
                androidComponents.onVariants { variant ->
                    tasks.withType(R8Task::class.java).configureEach {
                        dependsOn(killTask)
                    }
                    killTask.configure {
                    //    dependsOn(tasks.withType<KotlinCompile>())
                    }
                }
            }
            project.afterEvaluate {
                project.tasks.named("minifyReleaseWithR8") {
                    dependsOn(killTask)
                }
            }
        }
    }

    companion object {
        private const val R8_TASK_CLASS_NAME = "com.android.build.gradle.internal.tasks.R8Task"
    }
}
