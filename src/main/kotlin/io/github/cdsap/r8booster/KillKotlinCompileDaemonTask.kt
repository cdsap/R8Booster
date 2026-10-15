package io.github.cdsap.r8booster

import java.io.ByteArrayOutputStream
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
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
    abstract val command: Property<String>

    init {
        group = "verification"
        description = "Kills Kotlin compile daemon processes before running code shrinking"
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun killDaemons() {
        val output = ByteArrayOutputStream()
        val error = ByteArrayOutputStream()
        try {
            execOperations.exec {
                commandLine("sh", "-c", command.get())
                standardOutput = output
                errorOutput = error
                isIgnoreExitValue = true
            }
        } catch (ignored: Exception) {
        }
        logger.lifecycle("Kill Kotlin compile daemon command executed")
    }

    companion object {
        const val DEFAULT_COMMAND =
            "jps | grep -E \"KotlinCompileDaemon\" | awk '{print \$1}' | xargs -r kill -9"
    }
}
