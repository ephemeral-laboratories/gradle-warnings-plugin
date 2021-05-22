package garden.ephemeral.gradle.warnings.internal

import java.io.File

class WarningsParser {
    val messages = mutableListOf<CompilerMessage>();

    private var currentMessageBuilder: CompilerMessage.Builder? = null

    fun parse(file: File) {
        file.forEachLine(action = this::parseLine)
        addCurrentMessage()
    }

    fun parse(string: String) {
        string.lines().forEach(this::parseLine)
        addCurrentMessage()
    }

    private fun parseLine(line: String) {
        when {
            CompilerMessage.isSkippableLine(line) -> {}
            CompilerMessage.isFirstLine(line) -> {
                addCurrentMessage()
                currentMessageBuilder = CompilerMessage.builder(line)
            }
            else -> {
                currentMessageBuilder!!.appendLine(line)
            }
        }
    }

    private fun addCurrentMessage() {
        if (currentMessageBuilder != null) {
            messages.add(currentMessageBuilder!!.build())
            currentMessageBuilder = null;
        }
    }
}