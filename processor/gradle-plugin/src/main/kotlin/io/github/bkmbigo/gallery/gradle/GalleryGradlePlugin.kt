package io.github.bkmbigo.gallery.gradle

import io.github.bkmbigo.gallery.gradle.internal.Constants
import io.github.bkmbigo.gallery.gradle.internal.configuration.configureDesktopGallery
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

class GalleryGradlePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        // Apply GalleryKsp plugin
//        project.plugins.apply(Constants.GALLERY_KSP_PLUGIN)  // WORKAROUND: Apply plugin manually

        // Add gallery extension
        val extension = project.extensions.create("gallery", GalleryExtension::class.java, project)

        val kotlinProjectExtension = project.extensions.findByType(KotlinProjectExtension::class.java)

        project.afterEvaluate {
            kotlinProjectExtension?.let { kotlinProjectExtension ->
                configureDesktopGallery(project, extension, kotlinProjectExtension)
            }
        }
    }
}
