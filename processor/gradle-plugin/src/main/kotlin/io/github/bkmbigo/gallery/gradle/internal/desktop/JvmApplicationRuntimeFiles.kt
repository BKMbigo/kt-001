package io.github.bkmbigo.gallery.gradle.internal.desktop

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

internal class JvmApplicationRuntimeFiles(
    val allRuntimeJars: FileCollection,
    val mainJar: Provider<RegularFile>,
    private val taskDependencies: Array<Any>
) {
    operator fun component1() = allRuntimeJars
    operator fun component2() = mainJar

    fun <T: Task> configureUsageBy(task: T, fn: T.(JvmApplicationRuntimeFiles) -> Unit) {
        task.dependsOn(taskDependencies)
        task.fn(this)
    }

}

internal sealed class JvmApplicationRuntimeFilesProvider {
    abstract fun jvmApplicationRuntimeFiles(project: Project): JvmApplicationRuntimeFiles

    abstract class GradleJvmApplicationRuntimeFilesProvider: JvmApplicationRuntimeFilesProvider() {
        protected abstract val jarTaskName: String
        protected abstract val runtimeFiles: FileCollection

        override fun jvmApplicationRuntimeFiles(project: Project): JvmApplicationRuntimeFiles {
            val jarTask = project.tasks.named(jarTaskName, Jar::class.java)
            val mainJar = jarTask.flatMap { it.archiveFile }
            val runtimeJarFiles = project.objects.fileCollection().apply {
                from(mainJar)
                from(runtimeFiles.filter { it.path.endsWith(".jar") })
            }

            return JvmApplicationRuntimeFiles(runtimeJarFiles, mainJar, arrayOf(jarTask))
        }
    }

    class FromGradleSourceSet(private val project: Project, val sourceSet: SourceSet): GradleJvmApplicationRuntimeFilesProvider() {
        override val jarTaskName: String
            get() = "jar"

        override val runtimeFiles: FileCollection
            get() = sourceSet.runtimeClasspath


        private fun getMyJarTaskName(): String {
            return sourceSet.jarTaskName
        }
    }

    class FromKotlinMppTarget(private val target: KotlinJvmTarget): GradleJvmApplicationRuntimeFilesProvider() {
        override val jarTaskName: String
            get() = target.artifactsTaskName

        override val runtimeFiles: FileCollection
            get() = target.compilations.getByName("main").runtimeDependencyFiles
    }

    class FromGalleryKotlinMppTarget(private val target: KotlinJvmTarget): GradleJvmApplicationRuntimeFilesProvider() {
        override val jarTaskName: String
            get() = target.artifactsTaskName

        override val runtimeFiles: FileCollection
            get() = target.compilations.getByName("gallery").runtimeDependencyFiles
    }

    class Custom(
        private val runtimeJarFiles: FileCollection,
        private val mainJar: Provider<RegularFile>,
        private val taskDependencies: Array<Any>
    ): JvmApplicationRuntimeFilesProvider() {
        override fun jvmApplicationRuntimeFiles(project: Project): JvmApplicationRuntimeFiles =
            JvmApplicationRuntimeFiles(runtimeJarFiles, mainJar, taskDependencies)
    }


}
