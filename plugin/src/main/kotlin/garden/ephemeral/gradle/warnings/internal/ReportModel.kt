package garden.ephemeral.gradle.warnings.internal

data class ReportModel(
    val groups: Map<String, List<CompilerMessage>>,
    val totalWarningCount: Int
)