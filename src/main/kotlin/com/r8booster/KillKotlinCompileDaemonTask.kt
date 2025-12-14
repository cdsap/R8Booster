package com.r8booster

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class KillKotlinCompileDaemonTask : DefaultTask() {

    @get:Input
    abstract val kotlinDaemonKillInfo: Property<String>

    init {
        group = "verification"
        description = "Kills Kotlin compile daemon processes before running code shrinking"
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun killDaemons() {
        val output = kotlinDaemonKillInfo.get()
        logger.lifecycle("Kill Kotlin compile daemon command executed. Output: ${'$'}output")
    }
}
