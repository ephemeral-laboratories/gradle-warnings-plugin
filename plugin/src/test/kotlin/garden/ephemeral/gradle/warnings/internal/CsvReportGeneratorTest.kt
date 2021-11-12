package garden.ephemeral.gradle.warnings.internal

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.gradle.warnings.WarningsReport
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class CsvReportGeneratorTest {

    @Test
    fun `renders CSV rows`() {
        val model = createSingleWarningModel("You broke it")
        val csv = generateReport(model)
        assertThat(csv).isEqualTo("rawtypes,src/Blah.java:4,You broke it\n")
    }

    @Test
    fun `quotes the cell if it contains a comma`() {
        val model = createSingleWarningModel("You, why?")
        val csv = generateReport(model)
        assertThat(csv).isEqualTo("rawtypes,src/Blah.java:4,\"You, why?\"\n")
    }

    @Test
    fun `quotes the cell and escapes the quotes if it contains quotes`() {
        val model = createSingleWarningModel("You \"did\" it")
        val csv = generateReport(model)
        assertThat(csv).isEqualTo("rawtypes,src/Blah.java:4,\"You \"\"did\"\" it\"\n")
    }

    @Test
    fun `quotes the cell if it contains a multiple lines`() {
        val model = createSingleWarningModel("You did it\nYou did it again")
        val csv = generateReport(model)
        assertThat(csv).isEqualTo("rawtypes,src/Blah.java:4,\"You did it\nYou did it again\"\n")
    }

    private fun createSingleWarningModel(message: String): ReportModel = ReportModel(
        mapOf(
            "rawtypes" to listOf(
                CompilerMessage("src/Blah.java", "4", "warning", "rawtypes", message)
            )
        ), 1
    )

    @Test
    fun `renders nothing if there are no warnings`() {
        val model = ReportModel(mapOf(), 0)
        val csv = generateReport(model)
        assertThat(csv).isEqualTo("")
    }

    private fun generateReport(model: ReportModel): String {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply("garden.ephemeral.warnings")
        val task = project.tasks.getByName("warningsReport") as WarningsReport
        val report = task.reports.csv
        CsvReportGenerator(project).generateReport(report, model)
        val reportFile = report.outputLocation.get().asFile
        return reportFile.readText()
    }
}