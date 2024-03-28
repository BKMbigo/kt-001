package io.github.bkmbigo.gallery.processor.internal.verifiers

import io.github.bkmbigo.gallery.ksp.isPrivate
import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.processor.internal.Constants
import io.github.bkmbigo.gallery.processor.internal.GalleryProcessorException
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ComponentThemeWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ParamWrapper
import io.github.bkmbigo.gallery.processor.internal.utils.isComposable
import io.github.bkmbigo.gallery.processor.internal.utils.isTopLevelFunction

context(ProcessorEnvironment)
internal fun KSFunctionDeclaration.processComponentTheme(): ComponentThemeWrapper {

    if (!isComposable()) {
        logger.error("@GalleryComponentTheme can only be applied to a Composable function.", this)
        throw GalleryProcessorException()
    }


    if (isPrivate()) {
        logger.error("@GalleryComponentTheme can not be applied to @Composable functions.", this)
        throw GalleryProcessorException()
    }

    val paramsWithTypes = parameters.associateWith { it.type.resolve() }

    val composableFunctionParams = paramsWithTypes.filter { (parameter, type) ->
        type.isFunctionType &&
                !type.isSuspendFunctionType &&
                parameter.annotations.any { it.shortName.getShortName() == Constants.Annotations.SimpleName.Composable }
    }

    if (composableFunctionParams.isEmpty()) {
        logger.error("@GalleryComponentTheme has to have at least one composable function parameter.", this)
        throw GalleryProcessorException()
    } else if (composableFunctionParams.size > 1) {
        logger.error("@GalleryComponentTheme has more than one composable function parameter.", this)
    }

    val otherParameters = parameters - composableFunctionParams.keys

    if (otherParameters.any { !it.hasDefault }) {
        logger.error("@GalleryComponentTheme has unrecognized non-default parameters: ${otherParameters.filter{ !it.hasDefault }.joinToString { it.name?.asString() ?: "" }}")
        throw GalleryProcessorException()
    }

    val params = paramsWithTypes.mapNotNull { (param, type) ->
        if (composableFunctionParams.containsKey(param)) {
            null
        } else {
            ParamWrapper(
                identifier = null,
                paramName = null,
                name = param.name!!,
                type = type,
                defaultExpression = param.defaultExpression
            )
        }
    }


    return ComponentThemeWrapper(
        componentId = simpleName.getShortName(),
        importList = containingFile?.importDirectives ?: throw GalleryProcessorException(),
        kDoc = docString,
        fqName = qualifiedName ?: simpleName,
        contentParameterName = composableFunctionParams.keys.first().name?.getShortName() ?: throw GalleryProcessorException(),
        parameters = params
    )
}
