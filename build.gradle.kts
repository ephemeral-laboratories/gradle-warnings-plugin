plugins {
    id("com.diffplug.spotless") version "6.0.0"
}

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        }
    }
}

spotless {
    // ktlint >= 0.41.0 is broken
    // https://github.com/diffplug/spotless/issues/993
    val ktlintVersion = "0.40.0"
    kotlin {
        target(
            fileTree(rootDir) {
                include("*.kt", "*.kts")
                exclude("*.gradle.kts")
            }
        )
        ktlint(ktlintVersion)
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(ktlintVersion)
    }
}
