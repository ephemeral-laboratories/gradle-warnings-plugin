package garden.ephemeral.gradle.warnings.internal

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.gradle.api.Project
import org.gradle.api.reporting.SingleFileReport

class JsonReportGenerator(private val project: Project) {

    fun generateReport(report: SingleFileReport, model: ReportModel) {
        val file = report.outputLocation.asFile.get()
        project.mkdir(file.parentFile)

        file.bufferedWriter().use {
            val format = Json { prettyPrint = true }
            it.append(
                format.encodeToString(
                    buildJsonObject {
                        put("totalWarningCount", model.totalWarningCount)
                        put("groups", mapGroups(model.groups))
                    }
                )
            )
        }
    }

    private fun mapGroups(groups: Map<String, List<CompilerMessage>>): JsonArray = buildJsonArray {
        groups
            .map { (typeAndCategory, messages) ->
                buildJsonObject {
                    put(typeAndCategory, mapMessages(messages))
                }
            }
            .forEach { obj -> add(obj) }
    }

    private fun mapMessages(messages: List<CompilerMessage>): JsonArray = buildJsonArray {
        messages
            .map { message ->
                val relativePath = project.file(message.file)
                    .toRelativeString(project.projectDir).replace('\\', '/')
                buildJsonObject {
                    put("location", relativePath)
                    put("message", message.message)
                }
            }
            .forEach { obj -> add(obj) }
    }
}