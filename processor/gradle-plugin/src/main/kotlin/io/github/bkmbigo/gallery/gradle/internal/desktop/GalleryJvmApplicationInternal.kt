package io.github.bkmbigo.gallery.gradle.internal.desktop

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.SourceSet
import org.jetbrains.compose.desktop.application.dsl.JvmApplication
import org.jetbrains.compose.desktop.application.dsl.JvmApplicationBuildTypes
import org.jetbrains.compose.desktop.application.dsl.JvmApplicationDistributions
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import javax.inject.Inject

open class GalleryJvmApplicationInternal @Inject constructor(
    val name: String,
    val project: Project,
    objects: ObjectFactory
): JvmApplication() {
    internal val data: JvmApplicationData = objects.newInstance(JvmApplicationData::class.java)

    final override fun from(from: SourceSet) {
        data.jvmApplicationRuntimeFilesProvider = JvmApplicationRuntimeFilesProvider.FromGradleSourceSet(project, from)
    }

    final override fun from(from: KotlinTarget) {
        check(from is KotlinJvmTarget) {
            "Gallery: Non JVM kotlin MPP targets are not supported: ${from.javaClass.canonicalName} is not a subset of ${KotlinJvmTarget::class.java.canonicalName}"
        }
        data.jvmApplicationRuntimeFilesProvider = JvmApplicationRuntimeFilesProvider.FromKotlinMppTarget(from)
    }

    /*
    * Is similar to [from] but resolves the `gallery` configuration instead of `main`
    * */
    fun fromGalleryTarget(from: KotlinTarget) {
        check(from is KotlinJvmTarget) {
            "Gallery: Non JVM Gallery targets are not supported: ${from.javaClass.canonicalName} is not a subset of ${KotlinJvmTarget::class.java.canonicalName}"
        }
        data.jvmApplicationRuntimeFilesProvider = JvmApplicationRuntimeFilesProvider.FromGalleryKotlinMppTarget(from)
    }

    final override fun disableDefaultConfiguration() {
        data.isDefaultConfigurationEnabled = false
    }

    final override fun fromFiles(vararg files: Any) {
        data.fromFiles.from(*files)
    }

    final override fun dependsOn(vararg tasks: String) {
        data.dependenciesTaskNames.addAll(tasks)
    }

    final override fun dependsOn(vararg tasks: Task) {
        tasks.mapTo(data.dependenciesTaskNames) { it.path }
    }

    final override var mainClass: String? by data::mainClass
    final override val mainJar: RegularFileProperty by data::mainJar
    final override var javaHome: String by data::javaHome

    final override val args: MutableList<String> by data::args
    final override fun args(vararg args: String) {
        data.args.addAll(args)
    }

    final override val jvmArgs: MutableList<String> by data::jvmArgs
    final override fun jvmArgs(vararg jvmArgs: String) {
        data.jvmArgs.addAll(jvmArgs)
    }

    final override val nativeDistributions: JvmApplicationDistributions by data::nativeDistributions
    final override fun nativeDistributions(fn: Action<JvmApplicationDistributions>) {
        fn.execute(data.nativeDistributions)
    }

    final override val buildTypes: JvmApplicationBuildTypes by data::buildTypes
    final override fun buildTypes(fn: Action<JvmApplicationBuildTypes>) {
        fn.execute(data.buildTypes)
    }



}
