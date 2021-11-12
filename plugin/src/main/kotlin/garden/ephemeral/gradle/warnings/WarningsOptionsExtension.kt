package garden.ephemeral.gradle.warnings

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.OutputFile
import javax.inject.Inject

/**
 * Extension added to compile tasks containing options for recording warnings.
 */
abstract class WarningsOptionsExtension @Inject constructor(
    objects: ObjectFactory
) {
    @get:OutputFile
    val warningDump: RegularFileProperty = objects.fileProperty()
}