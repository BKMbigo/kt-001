package io.github.bkmbigo.gallery.gradle.internal.desktop

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.newInstance
import org.jetbrains.compose.desktop.application.dsl.JvmApplicationBuildTypes
import org.jetbrains.compose.desktop.application.dsl.JvmApplicationDistributions
import javax.inject.Inject

internal open class JvmApplicationData @Inject constructor(
    objects: ObjectFactory,
    private val providers: ProviderFactory
) {
    var jvmApplicationRuntimeFilesProvider: JvmApplicationRuntimeFilesProvider? = null
    var isDefaultConfigurationEnabled: Boolean = true
    val fromFiles: ConfigurableFileCollection = objects.fileCollection()
    val dependenciesTaskNames: MutableList<String> = ArrayList()
    var mainClass: String? = null
    val mainJar: RegularFileProperty = objects.fileProperty()

    private var customJavaHome: String? = null
    var javaHome: String
        get() = customJavaHome ?: System.getProperty("java.home") ?: error("galleryDesktop: `java.home` system property is not set")
        set(value) {
            customJavaHome = value
        }

    val javaHomeProvider: Provider<String>
        get() = providers.provider { javaHome }

    val args: MutableList<String> = ArrayList()
    val jvmArgs: MutableList<String> = ArrayList()
     val nativeDistributions: JvmApplicationDistributions = objects.newInstance()
    val buildTypes: JvmApplicationBuildTypes = objects.newInstance(JvmApplicationBuildTypes::class.java)

}
