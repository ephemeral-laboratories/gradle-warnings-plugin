package garden.ephemeral.gradle.warnings

import org.gradle.api.reporting.ConfigurableReport
import org.gradle.api.reporting.DirectoryReport
import org.gradle.api.reporting.ReportContainer
import org.gradle.api.tasks.Nested

/**
 * The reporting configuration for the [WarningsReport] task.
 */
interface WarningsReportsContainer : ReportContainer<ConfigurableReport?> {
    /**
     * The HTML report
     *
     * @return The HTML report
     */
    @get:Nested
    val html: DirectoryReport
}