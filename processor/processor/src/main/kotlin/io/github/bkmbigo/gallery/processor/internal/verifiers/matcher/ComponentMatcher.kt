package io.github.bkmbigo.gallery.processor.internal.verifiers.matcher

import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.StateComponentMap
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ComponentThemeWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ComponentWrapper

context(ProcessorEnvironment)
internal fun ComponentWrapper.matchParameters(stateComponentMap: StateComponentMap): ComponentMatched {
    val matchedParameters = parameters.associateWith { stateComponentMap.retrieveStateComponent(it.type, it.identifier) }

    return ComponentMatched(
        componentName = componentName,
        fqName = fqName,
        kDoc = kDoc,
        importList = importList,
        parameters = matchedParameters
    )
}

internal fun ComponentThemeWrapper.matchParameters(stateComponentMap: StateComponentMap): ComponentThemeMatched {
    val matchedParameters = parameters.associateWith { stateComponentMap.retrieveStateComponent(it.type, it.identifier) }

    return ComponentThemeMatched(
        componentId = componentId,
        fqName = fqName,
        kDoc = kDoc,
        importList = importList,
        parameters = matchedParameters
    )
}
