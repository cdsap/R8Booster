package com.r8booster

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import javax.inject.Inject
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.tasks.Input
import org.gradle.process.ExecException
import org.gradle.process.ExecOperations

interface KillKotlinCompileDaemonParameters : ValueSourceParameters {
    @get:Input
    val commands: Property<String>
}

abstract class KillKotlinCompileDaemonValueSource :
    ValueSource<String, KillKotlinCompileDaemonParameters> {

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String {
        val output = ByteArrayOutputStream()
        val error = ByteArrayOutputStream()
        return try {
            execOperations.exec {
                try {
                    commandLine("sh", "-c", parameters.commands.get())
                    standardOutput = output
                    errorOutput = error
                } catch (ignored: Exception) {
                }
            }
            String(output.toByteArray(), Charset.defaultCharset())
        } catch (e: ExecException) {
            String(error.toByteArray(), Charset.defaultCharset())
        }
    }

    companion object {
        const val DEFAULT_COMMAND =
            "jps | grep -E \"KotlinCompileDaemon\" | awk '{print \$1}' | xargs -r kill -9"
    }
}
