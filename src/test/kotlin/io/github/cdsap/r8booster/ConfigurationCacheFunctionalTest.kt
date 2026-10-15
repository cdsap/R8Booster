package io.github.cdsap.r8booster

import java.io.File
import kotlin.test.assertTrue
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class ConfigurationCacheFunctionalTest {

    @TempDir
    lateinit var testProjectDir: File

    @BeforeEach
    fun setup() {
        testProjectDir.resolve("settings.gradle.kts").writeText(
            """
            rootProject.name = "r8booster-cc-fixture"
            """.trimIndent()
        )
        testProjectDir.resolve("build.gradle.kts").writeText(
            """
            plugins {
                id("io.github.cdsap.r8booster")
            }
            """.trimIndent()
        )
    }

    @Test
    fun `killKotlinCompileDaemon is configuration cache compatible`() {
        val runner = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments(
                "killKotlinCompileDaemon",
                "--configuration-cache",
                "--configuration-cache-problems=fail",
                "--isolated-projects",
            )
            .forwardOutput()

        val first = runner.build()
        assertTrue(
            first.output.contains("Configuration cache entry stored"),
            "Expected configuration cache to be stored on first run:\n${first.output}"
        )

        val second = runner.build()
        assertTrue(
            second.output.contains("Configuration cache entry reused"),
            "Expected configuration cache HIT on second run:\n${second.output}"
        )
    }

    @Test
    fun `killKotlinCompileDaemon succeeds when the kill command fails`() {
        val marker = testProjectDir.resolve("kill-command-executed")
        testProjectDir.resolve("build.gradle.kts").appendText(
            """

            tasks.named<io.github.cdsap.r8booster.KillKotlinCompileDaemonTask>("killKotlinCompileDaemon") {
                command.set("touch '${marker.absolutePath}'; exit 42")
            }
            """.trimIndent()
        )

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments("killKotlinCompileDaemon")
            .forwardOutput()
            .build()

        assertTrue(marker.isFile, "Expected the kill command to run from the task action")
        assertTrue(
            result.output.contains("Kill Kotlin compile daemon command executed"),
            "Expected kill task lifecycle logging:\n${result.output}"
        )
    }
}
