package io.github.bkmbigo.gallery.processor.internal.verifiers

import io.github.bkmbigo.gallery.ksp.isPrivate
import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.processor.internal.GalleryProcessorException
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ScreenComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.utils.isBoolean
import io.github.bkmbigo.gallery.processor.internal.utils.isComposable
import io.github.bkmbigo.gallery.processor.internal.utils.isString
import io.github.bkmbigo.gallery.processor.internal.utils.isUnit

context(ProcessorEnvironment)
internal fun KSFunctionDeclaration.processScreenComponent(): ScreenComponentWrapper {

    if (!isComposable()) {
        logger.error("@GalleryScreen can only be applied to @Composable functions", this)
        throw GalleryProcessorException()
    }

    if (isPrivate()) {
        logger.error(
            "@GalleryScreen cannot be applied to private functions as they are not accessible from other files",
            this
        )
        throw GalleryProcessorException()
    }

    val componentParameters = parameters.filter { it.name?.getShortName() == "component" }

    if (componentParameters.isEmpty()) {
        logger.error("@GalleryScreen does not have a parameter with the name `component`", this)
        throw GalleryProcessorException()
    } else if (componentParameters.size > 1) {
        logger.error("@GalleryScreen has multiple parameters with the name `component`", this)
        throw GalleryProcessorException()
    }

    val componentParameter = componentParameters.first()
    val componentParameterType = componentParameter.type.resolve()

    if (!componentParameterType.isFunctionType) {
        logger.error("@GalleryScreen parameter `component` has to be a function parameter", this)
        throw GalleryProcessorException()
    } else if (!componentParameterType.annotations.any { it.shortName.getShortName() == "Composable" }) {
        logger.error("@GalleryScreen parameter `component` has to be a @Composable function parameter", this)
        throw GalleryProcessorException()
    }


    // Check for stateComponents parameter
    val stateComponentParameters = parameters.filter { it.name?.getShortName() == "stateComponents" }

    if (stateComponentParameters.isEmpty()) {
        logger.error("@GalleryScreen does not have a parameter with the name `stateComponents`", this)
        throw GalleryProcessorException()
    } else if (stateComponentParameters.size > 1) {
        logger.error("@GalleryScreen has multiple parameters with the name `stateComponents`", this)
        throw GalleryProcessorException()
    }

    val stateComponentParameter = stateComponentParameters.first()
    val stateComponentParameterType = stateComponentParameter.type.resolve()

    if (!stateComponentParameterType.isFunctionType) {
        logger.error("@GalleryScreen parameter `stateComponents` has to be a function parameter", this)
        throw GalleryProcessorException()
    } else if (!stateComponentParameterType.annotations.any { it.shortName.getShortName() == "Composable" }) {
        logger.error("@GalleryScreen parameter `stateComponents` has to be a @Composable function parameter", this)
        throw GalleryProcessorException()
    }

    val hasOnNavigateBackParameter: Boolean

    // Check for optional parameter `onNavigateBack`
    val onNavigateBackParameter = parameters.firstOrNull { it.name?.getShortName() == "onNavigateBack" }

    if (onNavigateBackParameter != null) {
        val onNavigateBackType = onNavigateBackParameter.type.resolve()

        if (!onNavigateBackType.isFunctionType) {
            logger.error(
                "@GalleryScreen cannot have a parameter `onNavigateBack` that is not the type (() -> Unit). Is not a function",
                this
            )
            throw GalleryProcessorException()
        } else if (onNavigateBackType.annotations.any { it.shortName.getShortName() == "Composable" }) {
            logger.error(
                "@GalleryScreen cannot have a parameter `onNavigateBack` that is takes a @Composable lambda",
                this
            )
            throw GalleryProcessorException()
        } else if (onNavigateBackParameter.type.element?.typeArguments?.size != 1) {
            logger.error(
                "@GalleryScreen cannot have a parameter `onNavigateBack` that is not the type (() -> Unit). Type arguments error",
                this
            )
            throw GalleryProcessorException()
        } else if (onNavigateBackParameter.type.element?.typeArguments?.getOrNull(0)?.type?.resolve()?.isUnit != true) {
            logger.error(
                "@GalleryScreen cannot have a parameter `onNavigateBack` that is not the type (() -> Unit). Unit check error",
                this
            )
            throw GalleryProcessorException()
        }

        hasOnNavigateBackParameter = true
    } else {
        hasOnNavigateBackParameter = false
    }

    val hasThemeStateComponentsParameter: Boolean

    // Check for optional parameter `onNavigateBack`
    val themeStateComponentsParameters = parameters.filter { it.name?.getShortName() == "themeStateComponents" }

    if (themeStateComponentsParameters.isNotEmpty()) {
        val themeStateComponentsParameter = themeStateComponentsParameters.first()

        val themeStateComponentsParameterType = themeStateComponentsParameter.type.resolve()

        if (!themeStateComponentsParameterType.isFunctionType) {
            logger.error(
                "@GalleryScreen cannot have a parameter `themeStateComponents` that is not the type (@Composable () -> Unit)",
                this
            )
            throw GalleryProcessorException()
        } else if (!themeStateComponentsParameterType.annotations.any { it.shortName.getShortName() == "Composable" }) {
            logger.error("@GalleryScreen the parameter `themeStateComponents` has to be a @Composable lambda", this)
            throw GalleryProcessorException()
        } else if (themeStateComponentsParameter.type.element?.typeArguments?.size != 1) {
            logger.error(
                "@GalleryScreen cannot have a parameter `themeStateComponents` that is not the type (@Composable () -> Unit). Has more type arguments",
                this
            )
            throw GalleryProcessorException()
        } else if (themeStateComponentsParameter.type.element?.typeArguments?.getOrNull(0)?.type?.resolve()?.isUnit != true) {
            logger.error(
                "@GalleryScreen cannot have a parameter `themeStateComponents` that is not the type (@Composable () -> Unit)",
                this
            )
            throw GalleryProcessorException()
        }

        hasThemeStateComponentsParameter = true
    } else {
        hasThemeStateComponentsParameter = false
    }

    // Check for optional parameter `componentName`
    val hasComponentNameParameter: Boolean

    val componentNameParameter = parameters.firstOrNull { it.name?.getShortName() == "componentName" }

    if (componentNameParameter != null) {
        val paramType = componentNameParameter.type.resolve()

        if (!paramType.isString) {
            logger.error(
                "@GalleryScreen cannot have a parameter `componentName` which is not of type kotlin.String",
                this
            )
            throw GalleryProcessorException()
        }

        hasComponentNameParameter = true
    } else {
        hasComponentNameParameter = false
    }

    // Check for optional parameter `hasStateComponents`
    val hasHasStateComponentsParameter: Boolean

    val hasStateComponentsParameter = parameters.firstOrNull { it.name?.getShortName() == "hasStateComponents" }

    if (hasStateComponentsParameter != null) {
        val paramType = hasStateComponentsParameter.type.resolve()

        if (!paramType.isBoolean) {
            logger.error(
                "@GalleryScreen cannot have a parameter `hasStateComponents` which is not of type kotlin.Boolean",
                this
            )
            throw GalleryProcessorException()
        }

        hasHasStateComponentsParameter = true
    } else {
        hasHasStateComponentsParameter = false
    }

    // Check for optional parameter `hasThemeComponents`
    val hasHasThemeComponentsParameter: Boolean

    val hasThemeComponentsParameter = parameters.firstOrNull { it.name?.getShortName() == "hasThemeComponents" }

    if (hasThemeComponentsParameter != null) {
        val paramType = hasThemeComponentsParameter.type.resolve()

        if (!paramType.isBoolean) {
            logger.error(
                "@GalleryScreen cannot have a parameter `hasThemeComponents` which is not of type kotlin.Boolean",
                this
            )
            throw GalleryProcessorException()
        }

        hasHasThemeComponentsParameter = true
    } else {
        hasHasThemeComponentsParameter = false
    }

    val remainingParameters =
        (parameters - stateComponentParameters.toSet() - componentParameter - themeStateComponentsParameters.toSet()).toMutableList()

    remainingParameters.remove(componentNameParameter)
    remainingParameters.remove(hasStateComponentsParameter)
    remainingParameters.remove(hasThemeComponentsParameter)
    remainingParameters.remove(onNavigateBackParameter)

    if (remainingParameters.any { !it.hasDefault }) {
        logger.error("@GalleryScreen has parameters with non-default values", this)
        throw GalleryProcessorException()
    }

    return ScreenComponentWrapper(
        fqName = qualifiedName!!,
        componentParameterName = "component",
        stateComponentsParameterName = "stateComponents",
        componentNameParameterName = if (hasComponentNameParameter) "componentName" else null,
        themeStateComponentsParameterName = if (hasThemeStateComponentsParameter) "themeStateComponents" else null,
        onNavigateBackParameterName = if (hasOnNavigateBackParameter) "onNavigateBack" else null,
        hasStateComponentsParameterName = if (hasHasStateComponentsParameter) "hasStateComponents" else null,
        hasThemeComponentParameterName = if (hasHasThemeComponentsParameter) "hasThemeComponents" else null
    )

}
