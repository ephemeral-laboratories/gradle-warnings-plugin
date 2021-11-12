package garden.ephemeral.gradle.warnings

import org.gradle.api.reporting.ConfigurableReport
import org.gradle.api.reporting.DirectoryReport
import org.gradle.api.reporting.ReportContainer
import org.gradle.api.reporting.SingleFileReport
import org.gradle.api.tasks.Nested

/**
 * The reporting configuration for the [WarningsReport] task.
 */
interface WarningsReportContainer : ReportContainer<ConfigurableReport?> {

    /**
     * The HTML report.
     */
    @get:Nested
    val html: DirectoryReport

    /**
     * The CSV report.
     */
    @get:Nested
    val csv: SingleFileReport

    /**
     * The JSON report.
     */
    @get:Nested
    val json: SingleFileReport
}