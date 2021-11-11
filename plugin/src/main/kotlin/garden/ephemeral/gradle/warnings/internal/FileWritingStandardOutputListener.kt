package garden.ephemeral.gradle.warnings.internal

import org.gradle.api.file.RegularFile
import org.gradle.api.logging.StandardOutputListener
import org.gradle.api.provider.Provider
import java.io.BufferedWriter
import java.io.Closeable

class FileWritingStandardOutputListener(
    private var file: Provider<RegularFile>
): StandardOutputListener, Closeable {

    private val writer: BufferedWriter by lazy {
        file.get().asFile.bufferedWriter()
    }

    override fun onOutput(output: CharSequence?) {
        if (output != null) {
            writer.append(output)
        }
    }

    override fun close() {
        writer.close()
    }
}