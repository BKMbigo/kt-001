package io.github.bkmbigo.gallery.gradle.internal.utils

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.util.GradleVersion
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/*
* Basically, there are two ways to check for the presence of an android plugin:
*   1. Check for an `android` extension
*   2. Check for either `com.android.application` or `com.android.library` or `com.android.dynamicprofile`
* */
internal val Project.hasAndroidPlugin: Boolean
    get(): Boolean = extensions.findByName("android") != null

internal val Project.hasComposeMultiplatformPlugin: Boolean
    get(): Boolean = plugins.hasPlugin("org.jetbrains.compose")

internal val Project.hasKotlinMultiplatformPlugin
    get(): Boolean = plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")

internal val Project.hasKotlinJvmPlugin
    get(): Boolean = plugins.hasPlugin("org.jetbrains.kotlin.jvm")

internal val Project.composeMultiplatformPluginOrNull
    get(): ComposePlugin? = plugins.findPlugin(ComposePlugin::class.java)

internal val Project.kotlinMultiplatformExtension
    get(): KotlinMultiplatformExtension = kotlinMultiplatformExtensionOrNull ?: error("Could not find KotlinMultiplatformExtension ($project)")

internal val Project.kotlinMultiplatformExtensionOrNull
    get(): KotlinMultiplatformExtension? = extensions.findByType(KotlinMultiplatformExtension::class.java)

internal val Project.javaSourceSets: SourceSetContainer
    get() = if (GradleVersion.current() < GradleVersion.version("7.1")) {
        convention.getPlugin(JavaPluginConvention::class.java).sourceSets
    } else {
        extensions.getByType(JavaPluginExtension::class.java).sourceSets
    }
