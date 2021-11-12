Gradle Warnings Plugin
======================

What is it?
-----------

A Gradle plugin to produce a report of the warnings output by the compiler.
This improves the visibility of these warnings, which many would just ignore if
using an IDE to compile the code.

Such reports exist in CI systems such as Jenkins, but it's useful to have ways
to get a warning report locally before pushing changes to code.

This was mostly created as a reaction to SonarQube not showing the actual
compiler warnings. SonarQube encourages developers to fix whatever _it_
considers to be an issue, while frequently overlooking compiler warnings.
This was causing developers to spend time just satisfying SonarQube rather
than improving the quality of the code or the speed of the build by reducing
the actual warning count.

Usage
-----

Using the plugins DSL:

```kotlin
plugins {
    id("garden.ephemeral.warnings") version "VERSION"
}
```

Using legacy plugin application:

```kotlin
buildscript {
    dependencies {
        classpath("garden.ephemeral.gradle.warnings:gradle-warnings-plugin:VERSION")
    }
}
apply(plugin = "garden.ephemeral.warnings")
```

Producing the report:

```shell
./gradlew warningsReport
```

Customising the report location:

```kotlin
tasks.named<garden.ephemeral.gradle.warnings.WarningsReport>("warningsReport") {
    reports {
        html.outputLocation.set(file("$buildDir/custom"))
    }
}
```

Disabling the report:

```kotlin
tasks.named<garden.ephemeral.gradle.warnings.WarningsReport>("warningsReport") {
    reports {
        html.required.set(false)
    }
}
```
