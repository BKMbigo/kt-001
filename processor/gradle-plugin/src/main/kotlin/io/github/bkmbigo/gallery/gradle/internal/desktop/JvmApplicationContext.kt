package io.github.bkmbigo.gallery.gradle.internal.desktop

import io.github.bkmbigo.gallery.gradle.internal.utils.*
import io.github.bkmbigo.gallery.gradle.internal.utils.hasKotlinJvmPlugin
import io.github.bkmbigo.gallery.gradle.internal.utils.hasKotlinMultiplatformPlugin
import io.github.bkmbigo.gallery.gradle.internal.utils.joinDashLowercaseNonEmpty
import io.github.bkmbigo.gallery.gradle.internal.utils.kotlinMultiplatformExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.jetbrains.compose.desktop.application.dsl.JvmApplicationBuildType
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

internal data class JvmApplicationContext(
    val project: Project,
    private val appInternal: GalleryJvmApplicationInternal,
    private val taskGroup: String = TASK_GROUP
) {
    val app: JvmApplicationData
        get() = appInternal.data

    val appDirName: String
        get() = joinDashLowercaseNonEmpty(appInternal.name, "main")

    val appTmpDir: Provider<Directory>
        get() = project.layout.buildDirectory.dir(
            "gallery/compose/tmp/$appDirName"
        )

    fun <T: Task> T.useAppRuntimeFiles(fn: T.(JvmApplicationRuntimeFiles) -> Unit) {
        val runtimeFiles = app.jvmApplicationRuntimeFilesProvider?.jvmApplicationRuntimeFiles(project)
            ?: JvmApplicationRuntimeFiles(
                allRuntimeJars = app.fromFiles,
                mainJar = app.mainJar,
                taskDependencies = app.dependenciesTaskNames.toTypedArray()
            )
        runtimeFiles.configureUsageBy(this, fn)
    }

    val tasks = JvmTasks(project, taskGroup)

    val packageNameProvider: Provider<String>
        get() = project.provider { appInternal.nativeDistributions.packageName ?: project.name }

    inline fun <reified T> provider(noinline fn: () -> T): Provider<T> =
        project.provider(fn)

    fun configureDefaultGalleryApp() {
        if (project.hasKotlinMultiplatformPlugin) {
            // TODO: find a better approach to configure the default app

            var isGalleryJvmConfigured = false
            project.kotlinMultiplatformExtension.targets.forEach { target ->
                if (target.platformType == KotlinPlatformType.jvm) {
                    if (!isGalleryJvmConfigured) {
                        appInternal.fromGalleryTarget(target)
                        isGalleryJvmConfigured = true
                    } else {
                        project.logger.error("Gallery: w: Error getting default configuration for Gallery Desktop Application, multiple Kotlin/JVM targets found. Unlike Compose Desktop, you cannot change this configuration")
                        appInternal.disableDefaultConfiguration()
                    }
                }
            }
        } else if (project.hasKotlinJvmPlugin) {
            val gallerySourceSet = project.javaSourceSets.getByName("gallery")
            appInternal.from(gallerySourceSet)
        }
    }


}
