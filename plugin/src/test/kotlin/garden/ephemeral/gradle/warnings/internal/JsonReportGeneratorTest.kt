package garden.ephemeral.gradle.warnings.internal

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.gradle.warnings.WarningsReport
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class JsonReportGeneratorTest {

    @Test
    fun `renders JSON`() {
        val model = createSingleWarningModel("You broke it")
        val csv = generateReport(model)
        assertThat(csv).isEqualTo("""
            {
                "totalWarningCount": 1,
                "groups": [
                    {
                        "rawtypes": [
                            {
                                "location": "src/Blah.java",
                                "message": "You broke it"
                            }
                        ]
                    }
                ]
            }
        """.trimIndent())
    }

    private fun createSingleWarningModel(message: String): ReportModel = ReportModel(
        1,
        mapOf(
            "rawtypes" to listOf(
                CompilerMessage("src/Blah.java", "4", "warning", "rawtypes", message)
            )
        ),
    )

    @Test
    fun `renders no warnings sensibly`() {
        val model = ReportModel(0, mapOf())
        val csv = generateReport(model)
        assertThat(csv).isEqualTo("""
            {
                "totalWarningCount": 0,
                "groups": [
                ]
            }
        """.trimIndent())
    }

    private fun generateReport(model: ReportModel): String {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply("garden.ephemeral.warnings")
        val task = project.tasks.getByName("warningsReport") as WarningsReport
        val report = task.reports.csv
        JsonReportGenerator(project).generateReport(report, model)
        val reportFile = report.outputLocation.get().asFile
        return reportFile.readText()
    }
}