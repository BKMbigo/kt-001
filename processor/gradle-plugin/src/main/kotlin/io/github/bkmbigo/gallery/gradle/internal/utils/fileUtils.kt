package io.github.bkmbigo.gallery.gradle.internal.utils

import org.gradle.api.file.Directory
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import java.io.File

internal fun Provider<Directory>.dir(relativePath: String): Provider<Directory> =
    map { it.dir(relativePath) }

internal val <T : FileSystemLocation> Provider<T>.ioFile: File
    get() = get().asFile

internal val <T : FileSystemLocation> Provider<T>.ioFileOrNull: File?
    get() = orNull?.asFile
