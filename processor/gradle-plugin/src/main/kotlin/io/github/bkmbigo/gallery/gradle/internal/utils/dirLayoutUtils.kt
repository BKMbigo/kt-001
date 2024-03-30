package io.github.bkmbigo.gallery.gradle.internal.utils

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Provider

internal val Project.jvmDirs: JvmDirectoriesProvider
    get() = JvmDirectoriesProvider(project.layout)

internal fun Task.jvmTmpDirForTask(): Provider<Directory> =
    project.jvmDirs.tmpDir(name)

internal class JvmDirectoriesProvider(
    private val layout: ProjectLayout
) {
    val composeDir: Provider<Directory>
        get() = layout.buildDirectory.dir("gallery/compose")

    fun tmpDir(name: String): Provider<Directory> =
        composeDir.dir("tmp/$name")
}
