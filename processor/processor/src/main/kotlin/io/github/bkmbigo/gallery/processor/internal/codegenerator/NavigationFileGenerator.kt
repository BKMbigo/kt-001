package io.github.bkmbigo.gallery.processor.internal.codegenerator

import io.github.bkmbigo.gallery.processor.internal.models.ComponentRegistrar
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment

context(ProcessorEnvironment)
internal class NavigationFileGenerator(
    private val componentRegistrar: ComponentRegistrar
) {

    fun generateFile(): String {

        val packageName = ""

        return """|
            |package 
            |
            |
        """.trimMargin()
    }
}
