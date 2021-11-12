package garden.ephemeral.gradle.warnings.internal

import org.gradle.api.Project
import org.gradle.api.reporting.SingleFileReport
import java.io.BufferedWriter

class CsvReportGenerator(private val project: Project) {

    fun generateReport(report: SingleFileReport, model: ReportModel) {
        val file = report.outputLocation.asFile.get()
        project.mkdir(file.parentFile)

        file.bufferedWriter().use {
            model.groups.forEach { (typeAndCategory, messages) ->
                messages.forEach { message ->
                    val relativePath = project.file(message.file)
                        .toRelativeString(project.projectDir).replace('\\', '/')
                    writeCsvRow(it, typeAndCategory, "$relativePath:${message.line}", message.message)
                }
            }
        }
    }

    private fun writeCsvRow(writer: BufferedWriter, vararg cells: String) {
        cells.forEachIndexed { index, cell ->
            if (index > 0) {
                writer.append(',')
            }
            writer.append(escapeCsvCell(cell))
        }
        writer.appendLine()
    }

    private fun escapeCsvCell(cell: String): String {
        return if (cell.contains("\"") || cell.contains(",") || cell.contains("\n")) {
            "\"" + cell.replace("\"", "\"\"") + "\""
        } else {
            cell
        }
    }
}