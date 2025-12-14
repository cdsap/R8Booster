plugins {
    kotlin("jvm") version "2.0.20"
    `java-gradle-plugin`
    `kotlin-dsl`
}

group = "com.r8booster"
version = "0.1.0"

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

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
            id = "com.r8booster.kotlin-daemon-killer"
            implementationClass = "com.r8booster.KotlinDaemonKillerPlugin"
            displayName = "Kotlin Compile Daemon Killer"
            description = "Kills Kotlin compile daemons before R8 tasks"
        }
    }
}
