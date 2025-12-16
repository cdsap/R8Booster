package io.github.cdsap.r8booster

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertTrue

class KotlinDaemonKillerPluginTest {

    @Test
    fun `kill task uses value source`() {
        val projectDir = createTempDirectory("killTaskProject").toFile()
        projectDir.writeBuildGradle(
            """
            plugins {
                id("com.r8booster.kotlin-daemon-killer")
            }

            repositories {
                mavenCentral()
                google()
            }
            """.trimIndent()
        )

        val result = gradleRunner(projectDir)
            .withArguments("killKotlinCompileDaemon", "--stacktrace")
            .build()

        assertTrue(result.output.contains("Kill Kotlin compile daemon command executed"))
        assertTrue(result.task(":killKotlinCompileDaemon")?.outcome == TaskOutcome.SUCCESS)
    }

    @Test
    fun `kill task is wired before r8-like tasks`() {
        val projectDir = createTempDirectory("r8WiringProject").toFile()
        projectDir.writeBuildGradle(
            """
            plugins {
                id("io.github.cdsap.r8booster")
            }

            repositories {
                mavenCentral()
            }

            tasks.register("minifyReleaseWithR8")
            """.trimIndent()
        )

        val result = gradleRunner(projectDir)
            .withArguments("minifyReleaseWithR8", "--dry-run")
            .build()

        val killIndex = result.output.indexOf(":killKotlinCompileDaemon")
        val r8Index = result.output.indexOf(":minifyReleaseWithR8")

        assertTrue(killIndex != -1 && r8Index != -1)
        assertTrue(killIndex < r8Index, "Kill task should run before R8 task")
    }

    private fun File.writeBuildGradle(contents: String) {
        File(this, "settings.gradle.kts").writeText("rootProject.name = \"test-project\"")
        File(this, "build.gradle.kts").writeText(contents)
    }

    private fun gradleRunner(projectDir: File): GradleRunner =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("--warning-mode", "all")
            .withPluginClasspath()
}
