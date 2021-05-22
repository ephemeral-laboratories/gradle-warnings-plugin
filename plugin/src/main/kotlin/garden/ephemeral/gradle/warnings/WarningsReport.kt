package garden.ephemeral.gradle.warnings

import garden.ephemeral.gradle.warnings.internal.CompilerMessage
import garden.ephemeral.gradle.warnings.internal.WarningsParser
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File


abstract class WarningsReport: DefaultTask() {

    abstract val warningDumps: ConfigurableFileCollection?
        @InputFiles
        get

    abstract val reportDir: DirectoryProperty?
        @OutputDirectory
        get

    @TaskAction
    fun generateReport() {
        val parser = WarningsParser()
        warningDumps!!.files
            .filter(File::exists)
            .forEach(parser::parse)

        val groups = parser.messages.groupBy { "${it.type}/${it.category}" }

        reportDir!!.get().file("index.html").asFile.writer().use {
            it.appendLine("<!DOCTYPE html>")
            it.appendHTML().html {
                head {
                    title("Warnings Report")
                }
                body {
                    if (groups.isEmpty()) {
                        p { text("No warnings!") }
                    } else {
                        p {
                            text("Total warnings: ")
                            strong { text(parser.messages.size) }
                        }
                        summaryTable(groups)
                        groups.forEach { (typeAndCategory, messages) ->
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