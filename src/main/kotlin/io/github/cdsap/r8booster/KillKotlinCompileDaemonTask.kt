package io.github.cdsap.r8booster

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault
abstract class KillKotlinCompileDaemonTask : DefaultTask() {

    @get:Inject
    abstract val execOperations: ExecOperations

    @get:Input
    abstract val commandLine: ListProperty<String>

    init {
        group = "verification"
        description = "Kills Kotlin compile daemon processes before running code shrinking"
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun killDaemons() {
        val arguments = commandLine.get()
        execOperations.exec {
            commandLine(arguments)
            isIgnoreExitValue = true
        }
        logger.lifecycle("Kill Kotlin compile daemon command executed")
    }
}
