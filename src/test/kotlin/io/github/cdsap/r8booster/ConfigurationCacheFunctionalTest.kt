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
                "--configuration-cache-problems=fail"
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
}
