package io.github.cdsap.r8booster

internal object KotlinDaemonKillCommand {
    val commandLine = listOf(
        "sh",
        "-c",
        "jps | grep -E \"KotlinCompileDaemon\" | awk '{print \$1}' | xargs -r kill -9",
    )
}
