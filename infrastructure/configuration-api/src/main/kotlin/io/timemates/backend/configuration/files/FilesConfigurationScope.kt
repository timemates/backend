package io.timemates.backend.configuration.files

import io.timemates.backend.configuration.annotations.TimeMatesDsl
import java.nio.file.Path

/**
 * This interface represents the configuration scope for handling files.
 * It provides methods for configuring the file system settings.
 *
 * @since 1.0
 */
@TimeMatesDsl
public interface FilesConfigurationScope {

    /**
     * Configures the file system to use local files with the provided settings.
     * This method sets up the local file system configuration.
     *
     * ```kotlin
     * localFiles {
     *     imagesPath(Path.of(URI("/path/to/images")))
     * }
     * ```
     *
     * @param block the block of code to configure the file system settings
     * @since 1.0
     */
    public fun localFiles(block: FileSystemScope.() -> Unit)

    /**
     * This interface represents the configuration scope for the file system settings.
     *
     * @since 1.0
     */
    @TimeMatesDsl
    public interface FileSystemScope {
        /**
         * Sets the path for storing images in the file system.
         *
         * ```kotlin
         * imagesPath(Path.of(URI("/path/to/images")))
         * ```
         *
         * @param path the path where images will be stored
         * @since 1.0
         */
        public fun imagesPath(path: Path)
    }
}
