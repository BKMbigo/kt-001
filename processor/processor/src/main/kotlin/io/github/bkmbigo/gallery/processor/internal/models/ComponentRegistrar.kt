package io.github.bkmbigo.gallery.processor.internal.models

import io.github.bkmbigo.gallery.processor.internal.GalleryProcessorException
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ParamWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ScreenComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.StateComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.verifiers.matcher.ComponentMatched
import io.github.bkmbigo.gallery.processor.internal.verifiers.matcher.ComponentThemeMatched

context(ProcessorEnvironment)
internal class ComponentRegistrar {

    private val componentList: MutableList<ComponentMatched> = mutableListOf()
    private val componentThemeList: MutableList<ComponentThemeMatched> = mutableListOf()

    private val themeStateComponentMap: MutableMap<String, Pair<ParamWrapper, StateComponentWrapper>> = mutableMapOf()

    private var screenComponent: ScreenComponentWrapper? = null

    val screen
        get() = screenComponent

    val components = componentList.asSequence()

    val hasScreen
        get() = screenComponent != null

    val themeStateComponents
        get() = themeStateComponentMap.values.associate { it }

    fun addComponent(
        componentMatched: ComponentMatched
    ) {
        componentList.add(componentMatched)
    }

    fun addComponentTheme(
        componentThemeMatched: ComponentThemeMatched
    ) {
        // Add a check to check for matching parameter names
        componentThemeMatched.parameters.forEach { (param, stateComponentWrapper) ->
            val paramName = param.name.getShortName()
            if (themeStateComponentMap.containsKey(paramName)) {
                // Cannot Add as there contains another themeState with the same parameter name
                logger.error("@GalleryThemeComponent has conflicting parameter names with another @GalleryThemeComponent")
                throw GalleryProcessorException()
            } else {
                themeStateComponentMap[paramName] = param to stateComponentWrapper
            }
        }
        componentThemeList.add(componentThemeMatched)
    }

    fun registerGalleryScreen(
        screen: ScreenComponentWrapper
    ): Boolean =
        if (screenComponent != null) {
            false
        } else {
            screenComponent = screen
            true
        }


}
