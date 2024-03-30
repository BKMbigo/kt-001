package io.github.bkmbigo.gallery.gradle.targets

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class GalleryTarget @Inject constructor(
    project: Project
) {
    open val enabled: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)
}
