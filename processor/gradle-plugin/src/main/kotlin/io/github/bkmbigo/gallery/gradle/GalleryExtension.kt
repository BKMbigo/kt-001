package io.github.bkmbigo.gallery.gradle

import io.github.bkmbigo.gallery.gradle.targets.AndroidGalleryTarget
import io.github.bkmbigo.gallery.gradle.targets.ComposeWebGalleryTarget
import io.github.bkmbigo.gallery.gradle.targets.DesktopGalleryTarget
import org.gradle.api.Action
import org.gradle.api.Incubating
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

abstract class GalleryExtension @Inject constructor(
    objectFactory: ObjectFactory,
    project: Project
): ExtensionAware {
    internal val androidGalleryTarget: AndroidGalleryTarget = objectFactory.newInstance(project)
    internal val desktopGalleryTarget: DesktopGalleryTarget = objectFactory.newInstance(project)
    internal val jsGalleryTarget: ComposeWebGalleryTarget = objectFactory.newInstance(project)
    internal val wasmJsGalleryTarget: ComposeWebGalleryTarget = objectFactory.newInstance(project)
    internal val devOptions: DevOptions = objectFactory.newInstance(project)

    // Should this be a required value?
    val modulePackageName: Provider<String> = objectFactory.property(String::class.java).convention("")

    @Incubating
    fun androidTarget(fn: Action<AndroidGalleryTarget>) {
        fn.execute(androidGalleryTarget)
    }

    @Incubating
    fun desktopTarget(fn: Action<DesktopGalleryTarget>) {
        fn.execute(desktopGalleryTarget)
    }

    @Incubating
    fun jsTarget(fn: Action<ComposeWebGalleryTarget>) {
        fn.execute(jsGalleryTarget)
    }

    @Incubating
    fun wasmJsTarget(fn: Action<ComposeWebGalleryTarget>) {
        fn.execute(wasmJsGalleryTarget)
    }

    @Incubating
    fun devOptions(fn: Action<DevOptions>) {
        fn.execute(devOptions)
    }


}
