plugins {
    kotlin("jvm") version "2.0.20"
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.0.0-rc-1"
}

group = "io.github.cdsap.r8booster"
version = "0.0.1"



dependencies {
    compileOnly("com.android.tools.build:gradle:8.13.1")
    implementation(gradleApi())

    testImplementation(kotlin("test"))
    testImplementation(gradleTestKit())
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("kotlinDaemonKiller") {
            id = "io.github.cdsap.r8booster"
            implementationClass = "io.github.cdsap.r8booster.KotlinDaemonKillerPlugin"
            displayName = "Kotlin Compile Daemon Killer"
            description = "Kills Kotlin compile daemons before R8 tasks"
        }
    }
}

pluginBundle {
    website = "https://github.com/cdsap/R8Booster"
    vcsUrl = "https://github.com/cdsap/R8Booster"
    tags = listOf("android", "r8")
}
