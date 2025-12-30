# R8Booster

A Gradle plugin that automatically kills Kotlin compile daemons before R8 tasks to improve build performance and prevent memory issues during code shrinking.

## Overview

R8Booster is a Gradle plugin designed for Android projects that use Kotlin. It automatically terminates Kotlin compile daemon processes before R8 (code shrinking) tasks execute, which can help:

- **Reduce memory pressure** during R8 execution
- **Improve build reliability** by preventing daemon-related conflicts
- **Optimize build performance** by freeing up resources before intensive tasks

## Installation

### Using the Plugin DSL

Add the plugin to your root `build.gradle.kts` or `build.gradle`:

```kotlin
plugins {
    id("io.github.cdsap.r8booster") version "0.0.5"
}
```

### Using Legacy Plugin Application

```kotlin
buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.github.cdsap.r8booster:r8booster:0.0.5")
    }
}

apply plugin: "io.github.cdsap.r8booster"
```

## Usage

Once applied, the plugin automatically:

1. Registers a `killKotlinCompileDaemon` task
2. Configures R8 tasks to depend on the kill task, ensuring daemons are terminated before code shrinking


## How It Works

The plugin:

1. **Registers a kill task**: Creates a `killKotlinCompileDaemon` task that terminates Kotlin compile daemon processes
2. **Hooks into R8 tasks**: Automatically makes R8 tasks depend on the kill task
3. **Uses shell commands**: Executes a command to find and kill Kotlin compile daemon processes:
   ```bash
   jps | grep -E "KotlinCompileDaemon" | awk '{print $1}' | xargs -r kill -9
   ```

### Supported Tasks

The plugin automatically wires the kill task before:
- `R8Task` instances (all variants)
- `minifyReleaseWithR8` task

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Links

- **Website**: https://github.com/cdsap/R8Booster
- **VCS URL**: https://github.com/cdsap/R8Booster
