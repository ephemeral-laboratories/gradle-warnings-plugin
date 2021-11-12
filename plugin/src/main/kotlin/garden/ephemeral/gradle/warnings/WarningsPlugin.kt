/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package garden.ephemeral.gradle.warnings

import garden.ephemeral.gradle.warnings.internal.FileWritingStandardOutputListener
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.provider.Provider
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.internal.instantiation.generator.ManagedObjectFactory
import org.gradle.internal.state.ModelObject

/**
 * Main entry point for the Gradle warnings plugin.
 */
class WarningsPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(ReportingBasePlugin::class.java)

        val compileTasks = project.tasks.withType(JavaCompile::class.java)
        val warningDumps = mutableListOf<Provider<RegularFile>>()

        compileTasks.configureEach { task ->
            val warningsOptions = task.extensions.create("warnings",
                WarningsOptionsExtension::class.java,
                project.objects)

            // HACK: For some reason `task.extensions.create` doesn't set the task as owner.
            //       This private API does so explicitly.
            ManagedObjectFactory.attachOwner(warningsOptions, task as ModelObject, "warnings")

            warningsOptions.warningDump.set(project.layout.buildDirectory.file("${task.name}.stderr"))
            warningDumps.add(warningsOptions.warningDump)

            val listener = FileWritingStandardOutputListener(warningsOptions.warningDump)
            task.logging.addStandardErrorListener(listener)
            task.doLast { listener.close() }
        }

        project.tasks.register("warningsReport", WarningsReport::class.java) { task ->
            task.dependsOn(compileTasks)

            task.warningDumps.setFrom(warningDumps)

            val reportingExtension = project.extensions.getByName(ReportingExtension.NAME) as ReportingExtension
            task.reports.html.outputLocation.set(reportingExtension.baseDirectory.dir("warnings"))
            task.reports.html.required.set(true)
        }
    }
}
