package garden.ephemeral.gradle.warnings.internal

import garden.ephemeral.gradle.warnings.WarningsReportContainer
import org.gradle.api.Task
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.reporting.ConfigurableReport
import org.gradle.api.reporting.DirectoryReport
import org.gradle.api.reporting.internal.TaskGeneratedSingleDirectoryReport
import org.gradle.api.reporting.internal.TaskReportContainer

open class DefaultWarningsReportContainer(task: Task, callbackActionDecorator: CollectionCallbackActionDecorator) :
    TaskReportContainer<ConfigurableReport>(ConfigurableReport::class.java, task, callbackActionDecorator),
    WarningsReportContainer {

    override val html: DirectoryReport
        get() = getByName("html") as DirectoryReport

    init {
        super.add(TaskGeneratedSingleDirectoryReport::class.java, "html", task, "index.html")
    }
}