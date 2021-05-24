Gradle Warnings Plugin
======================

What is it?
-----------

A Gradle plugin to produce a report of the warnings output by the compiler.

Such things exist in CI systems like Jenkins, but it's often useful to have ways
to report on such things locally before committing new code.

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
tasks.warningsReport {
    reports {
        html.outputLocation.set(file("$buildDir/custom"))
    }
}
```

Disabling the report:

```kotlin
tasks.warningsReport {
    reports {
        html.required.set(false)
    }
}
```
