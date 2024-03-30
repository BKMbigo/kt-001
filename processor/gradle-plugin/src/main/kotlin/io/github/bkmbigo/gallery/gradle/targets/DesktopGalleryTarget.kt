package io.github.bkmbigo.gallery.gradle.targets

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import org.jetbrains.compose.desktop.application.dsl.*
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import javax.inject.Inject

abstract class DesktopGalleryTarget @Inject constructor(
    project: Project,
    objectFactory: ObjectFactory
): GalleryTarget(project) {

    internal val jvmArgs: MutableSet<String> = mutableSetOf()
    internal val nativeDistributions: JvmApplicationDistributions = objectFactory.newInstance()
}


