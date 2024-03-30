package io.github.bkmbigo.gallery.gradle.targets

import org.gradle.api.Project
import javax.inject.Inject

abstract class AndroidGalleryTarget @Inject constructor(
    project: Project
): GalleryTarget(project) {

}
