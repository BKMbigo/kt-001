package io.github.bkmbigo.gallery.processor.internal.models

import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ScreenComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.verifiers.matcher.ComponentMatched
import io.github.bkmbigo.gallery.processor.internal.verifiers.matcher.ComponentThemeMatched

context(ProcessorEnvironment)
internal class ComponentRegistrar {

    private val componentList: MutableList<ComponentMatched> = mutableListOf()
    private val componentThemeList: MutableList<ComponentThemeMatched> = mutableListOf()

    private var screenComponent: ScreenComponentWrapper? = null

    val screen
        get() = screenComponent

    val components = componentList.asSequence()

    val hasScreen
        get() = screenComponent != null

    fun addComponent(
        componentMatched: ComponentMatched
    ) {
        componentList.add(componentMatched)
    }

    fun addComponentTheme(
        componentThemeMatched: ComponentThemeMatched
    ) {
        componentThemeList.add(componentThemeMatched)
    }

    fun registerGalleryScreen(
        screenComponent: ScreenComponentWrapper
    ): Boolean =
        if (this.screenComponent != null) {
            false
        } else {
            this.screenComponent = screenComponent
            true
        }


}
