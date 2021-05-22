/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package garden.ephemeral.gradle.warnings

import assertk.assertThat
import assertk.assertions.isNotNull
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

/**
 * A simple unit test for the 'garden.ephemeral.gradle.warnings.greeting' plugin.
 */
class GradleWarningsPluginTest {

    @Test
    fun `plugin extends compile task`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply("garden.ephemeral.warnings")

        assertThat(project.tasks.getByName("compileJava")
            .extensions.findByName("warnings")).isNotNull()
    }

    @Test
    fun `plugin registers task`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("garden.ephemeral.warnings")

        assertThat(project.tasks.findByName("warningsReport")).isNotNull()
    }
}
