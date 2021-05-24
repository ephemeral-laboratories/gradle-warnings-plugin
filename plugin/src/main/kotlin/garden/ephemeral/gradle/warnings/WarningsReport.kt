package garden.ephemeral.gradle.warnings

import garden.ephemeral.gradle.warnings.internal.CompilerMessage
import garden.ephemeral.gradle.warnings.internal.WarningsParser
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.reflect.Instantiator
import java.io.File
import javax.inject.Inject


abstract class WarningsReport: DefaultTask() {

    @get:InputFiles
    abstract val warningDumps: ConfigurableFileCollection

    @get:Inject
    protected abstract val instantiator: Instantiator

    @get:Inject
    protected abstract val callbackActionDecorator: CollectionCallbackActionDecorator

    @get:Nested
    val reports: WarningsReportsContainer by lazy {
        instantiator.newInstance(DefaultWarningsReportsContainer::class.java, this, callbackActionDecorator)
    }

    open fun reports(configureAction: Action<in WarningsReportsContainer>): WarningsReportsContainer {
        configureAction.execute(reports)
        return reports
    }

    @TaskAction
    fun generateReport() {
        val parser = WarningsParser()
        warningDumps.files
            .filter(File::exists)
            .forEach(parser::parse)

        val groups = parser.messages.groupBy { "${it.type}/${it.category}" }
        if (reports.html.required.get()) {
            generateHtmlReport(groups, parser)
        }
    }

    private fun generateHtmlReport(
        groups: Map<String, List<CompilerMessage>>,
        parser: WarningsParser
    ) {
        val htmlDir = reports.html.outputLocation.get()
        project.mkdir(htmlDir.asFile)

        val indexFile = reports.html.entryPoint
        indexFile.writer().use {
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