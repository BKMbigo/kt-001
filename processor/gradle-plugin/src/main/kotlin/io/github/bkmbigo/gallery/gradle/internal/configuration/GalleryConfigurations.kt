package io.github.bkmbigo.gallery.gradle.internal.configuration

import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.targets

internal object GalleryConfigurations {

    fun createGalleryConfiguration(
        kotlinExtension: KotlinProjectExtension,
        platform: KotlinPlatformType
    ) {
        // This method should not be used to create android configurations
        if (platform != KotlinPlatformType.androidJvm) {
            val targets = kotlinExtension.targets.filter { it.platformType == platform }
            if (targets.isNotEmpty()) {
                val mainCompilations = targets.map { it to it.compilations.findByName("main") }.filter { it.second != null }
                mainCompilations.forEach { (target, mainCompilation) ->
                    val compilation = target.compilations.create("gallery") {
                        associateWith(mainCompilation!!)
                    }
                }
            }
        }
    }

}
