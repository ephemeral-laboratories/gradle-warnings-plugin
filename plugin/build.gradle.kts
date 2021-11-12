plugins {
    `java-gradle-plugin`
    id("org.jetbrains.kotlin.jvm") version "1.4.31"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    }
}

group = "garden.ephemeral.gradle.warnings"
version = "0.1.0"

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.24")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

gradlePlugin {
    val warnings by plugins.creating {
        id = "garden.ephemeral.warnings"
        implementationClass = "garden.ephemeral.gradle.warnings.WarningsPlugin"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])
configurations["functionalTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}

tasks.check {
    dependsOn(functionalTest)
}
