package io.github.bkmbigo.gallery.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import javax.inject.Inject

abstract class DevOptions @Inject constructor(
    project: Project
) {

    val enableCompilerLogs: Provider<Boolean> = project.objects.property(Boolean::class.java).convention(false)

    val printCompilerLogs: Provider<Boolean> = project.objects.property(Boolean::class.java).convention(false)
    val printIncrementalLogs: Provider<Boolean> = project.objects.property(Boolean::class.java).convention(false)

}
