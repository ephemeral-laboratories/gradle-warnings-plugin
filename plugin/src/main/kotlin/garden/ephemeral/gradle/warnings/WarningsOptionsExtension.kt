package garden.ephemeral.gradle.warnings

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory

/**
 * Extension added to compile tasks containing options for recording warnings.
 */
abstract class WarningsOptionsExtension(
    objectFactory: ObjectFactory
) {
    val warningDump: RegularFileProperty = objectFactory.fileProperty()


}