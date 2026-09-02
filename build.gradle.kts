import org.gradle.plugin.compatibility.compatibility

plugins {
    kotlin("jvm") version "2.0.20"
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "2.1.1"
}

group = "io.github.cdsap.r8booster"
version = "0.0.5"

dependencies {
    compileOnly("com.android.tools.build:gradle:8.13.1")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.21")
    implementation(gradleApi())

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(gradleTestKit())
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    website.set("https://github.com/cdsap/R8Booster")
    vcsUrl.set("https://github.com/cdsap/R8Booster")
    plugins {
        create("kotlinDaemonKiller") {
            id = "io.github.cdsap.r8booster"
            implementationClass = "io.github.cdsap.r8booster.KotlinDaemonKillerPlugin"
            displayName = "Kotlin Compile Daemon Killer"
            description = "Kills Kotlin compile daemons before R8 tasks"
            tags.set(listOf("android", "r8"))
            compatibility {
                features {
                    // Declared true only after ConfigurationCacheFunctionalTest proves a CC HIT.
                    configurationCache = true
                }
            }
        }
    }
}
