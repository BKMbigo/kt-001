package io.github.bkmbigo.gallery.gradle.internal.configuration

import org.gradle.api.Project

/*
* Navigation should be an implementation detail (User's should not know what navigation library Gallery uses, but just that it works)
*
* For now, I will use voyager due to its support for multiple targets.
* */

private const val VOYAGER_VERSION = "1.0.0"

private const val VOYAGER_NAVIGATION = "cafe.adriel.voyager:voyager-navigator:$VOYAGER_VERSION"

internal fun configureGalleryNavigation(
    project: Project,
    configurationName: String
) {
    // Adds the artifact to the configuration
    project.dependencies.add(
        configurationName,
        VOYAGER_NAVIGATION
    )
}
