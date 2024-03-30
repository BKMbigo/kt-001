package io.github.bkmbigo.gallery.gradle.targets

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class ComposeWebGalleryTarget @Inject constructor(
    project: Project
): GalleryTarget(project) {

    val localPort: Property<String> = project.objects.property(String::class.java).convention("8080")

}
