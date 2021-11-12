package garden.ephemeral.gradle.warnings.internal

data class ReportModel(
    val totalWarningCount: Int,
    val groups: Map<String, List<CompilerMessage>>
)