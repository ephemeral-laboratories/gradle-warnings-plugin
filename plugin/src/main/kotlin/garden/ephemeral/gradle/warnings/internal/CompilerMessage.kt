package garden.ephemeral.gradle.warnings.internal

data class CompilerMessage(
    val file: String,
    val line: String,
    val type: String,
    val category: String,
    val message: String,
) {
    companion object {
        private var compilerMessagePattern = Regex(
            "^(\\S+):(\\d+): (warning|error): \\[(\\w+)] (.*)$");
        private var skippableMessagePattern = Regex(
            "^\\d+ warnings$")

        fun isSkippableLine(line: String): Boolean {
            return line.matches(skippableMessagePattern)
        }

        fun isFirstLine(line: String): Boolean {
            return line.matches(compilerMessagePattern)
        }

        fun builder(firstLine: String): Builder {
            val match = compilerMessagePattern.matchEntire(firstLine)!!
            val (file, line, type, category, message) = match.destructured
            return Builder(file, line, type, category, message)
        }
    }

    class Builder(
        private val file: String,
        private val line: String,
        private val type: String,
        private val category: String,
        private var message: String,
    ) {
        fun appendLine(line: String) {
            message += "\n$line"
        }

        fun build(): CompilerMessage {
            return CompilerMessage(file, line, type, category, message)
        }
    }
}