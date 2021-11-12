package garden.ephemeral.gradle.warnings.internal

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.matches
import garden.ephemeral.gradle.warnings.WarningsReport
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import java.io.File

class HtmlReportGeneratorTest {

    @Test
    fun `renders warning table`() {
        val model = ReportModel(
            1,
            mapOf(
                "rawtypes" to listOf(
                    CompilerMessage("src/Blah.java", "4", "warning", "rawtypes", "You broke it")
                )
            )
        )
        val reportDir = generateReport(model)
        val html = reportDir.resolve("index.html").readText()
        assertThat(html).all {
            contains("<p>Total warnings: <strong>1</strong></p>")
            contains("<td><a href=\"#rawtypes\">rawtypes</a></td>")
            contains("<td style=\"text-align: right\">1</td>")
            contains("<th style=\"text-align: left\" colspan=\"2\">rawtypes</th>")
            contains("<td>src/Blah.java:4</td>")
            matches(
                Regex(
                    ".*<td>\\n\\s*<pre>You broke it</pre>\\n\\s*</td>.*",
                    setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL)
                )
            )
        }
    }

    @Test
    fun `renders a different message when there are no warnings`() {
        val model = ReportModel(0, mapOf())
        val reportDir = generateReport(model)
        val html = reportDir.resolve("index.html").readText()
        assertThat(html).contains("<p>No warnings!</p>")
    }

    @Test
    fun `escapes special characters`() {
        val model = ReportModel(
            1,
            mapOf(
                "rawtypes" to listOf(
                    CompilerMessage("src/Blah.java", "4", "warning", "rawtypes", "Thunderbolt & Lightning")
                )
            )
        )
        val reportDir = generateReport(model)
        val html = reportDir.resolve("index.html").readText()
        assertThat(html).contains("<pre>Thunderbolt &amp; Lightning</pre>")
    }

    private fun generateReport(model: ReportModel): File {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply("garden.ephemeral.warnings")
        val task = project.tasks.getByName("warningsReport") as WarningsReport
        val report = task.reports.html
        HtmlReportGenerator(project).generateReport(report, model)
        return report.outputLocation.get().asFile
    }

}