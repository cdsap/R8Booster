package io.github.cdsap.r8booster

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.tasks.R8Task
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import java.util.Locale

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

            plugins.withType(AppPlugin::class.java) {
                val androidComponents =
                    extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
                androidComponents.onVariants(androidComponents.selector().withBuildType("release")) { variant ->

                    tasks.withType<R8Task>().configureEach {
                        dependsOn(killTask)
                    }
                }
            }
        }
    }

    companion object {
        private const val R8_TASK_CLASS_NAME = "com.android.build.gradle.internal.tasks.R8Task"
    }
}
