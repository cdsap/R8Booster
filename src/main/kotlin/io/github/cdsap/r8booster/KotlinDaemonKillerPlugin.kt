package io.github.cdsap.r8booster
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.tasks.R8Task
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

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

            plugins.withType(AppPlugin::class.java) {
                val androidComponents =
                    extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
                androidComponents.onVariants(androidComponents.selector().withBuildType("release")) { variant ->

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

    companion object {
        private const val R8_TASK_CLASS_NAME = "com.android.build.gradle.internal.tasks.R8Task"
    }
}
