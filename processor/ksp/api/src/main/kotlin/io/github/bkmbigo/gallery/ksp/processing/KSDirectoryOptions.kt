package io.github.bkmbigo.gallery.ksp.processing

import java.io.File

interface KSDirectoryOptions {

    val cachesDir: File

    val kotlinOutputDir: File
    val resourceOutputDir: File
}
