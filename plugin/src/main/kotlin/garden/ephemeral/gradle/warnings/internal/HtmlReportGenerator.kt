package garden.ephemeral.gradle.warnings.internal

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.gradle.api.Project
import org.gradle.api.reporting.DirectoryReport

class HtmlReportGenerator(private val project: Project) {

    fun generateReport(report: DirectoryReport, model: ReportModel) {
        val htmlDir = report.outputLocation.asFile.get()
        project.mkdir(htmlDir)

        val indexFile = report.entryPoint
        indexFile.bufferedWriter().use {
            it.appendLine("<!DOCTYPE html>")
            it.appendHTML().html {
                head {
                    title("Warnings Report")
                }
                body {
                    if (model.totalWarningCount == 0) {
                        p { text("No warnings!") }
                    } else {
                        p {
                            text("Total warnings: ")
                            strong { text(model.totalWarningCount) }
                        }
                        summaryTable(model.groups)
                        model.groups.forEach { (typeAndCategory, messages) ->
                            warningsTable(typeAndCategory, messages)
                        }
                    }
                }
            }
        }
    }

    private fun BODY.summaryTable(groups: Map<String, List<CompilerMessage>>) {
        table {
            thead {
                tr {
                    th {
                        style = "text-align: left"
                        text("Type and category")
                    }
                    th {
                        style = "text-align: right"
                        text("Count")
                    }
                }
            }
            tbody {
                groups.forEach { (typeAndCategory, messages) ->
                    tr {
                        td {
                            a("#$typeAndCategory") {
                                text(typeAndCategory)
                            }
                        }
                        td {
                            style = "text-align: right"
                            text(messages.size)
                        }
                    }
                }
            }
        }
    }

    private fun BODY.warningsTable(typeAndCategory: String, messages: List<CompilerMessage>) {
        table {
            id = typeAndCategory
            thead {
                tr {
                    th {
                        style = "text-align: left"
                        colSpan = "2"
                        text(typeAndCategory)
                    }
                }
            }
            tbody {
                messages.forEach { message ->
                    tr {
                        td {
                            val relativePath = project.file(message.file)
                                .toRelativeString(project.projectDir).replace('\\', '/')
                            text("$relativePath:${message.line}")
                        }
                        td {
                            pre {
                                text(message.message)
                            }
                        }
                    }
                }
            }
        }
    }

}