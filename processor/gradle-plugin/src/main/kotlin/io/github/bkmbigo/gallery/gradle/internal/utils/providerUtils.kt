package io.github.bkmbigo.gallery.gradle.internal.utils

import org.gradle.api.Task
import org.gradle.api.provider.Provider

internal inline fun <reified T> Task.provider(noinline fn: () -> T): Provider<T> =
    project.provider(fn)
