package io.github.bkmbigo.gallery.ksp.processing

import java.io.File

interface GalleryKSOptions {

    /**
     * Used to determine whether to generate navigation and main files
     * */
    val isGalleryConfiguration: Boolean

    /**
     * Used to determine the package name for generated files
     * */
    val modulePackageName: String

    /**
     * Used in caching
     * */
    val cachesDir: File
    val kotlinOutputDir: File
    val resourceOutputDir: File
}
