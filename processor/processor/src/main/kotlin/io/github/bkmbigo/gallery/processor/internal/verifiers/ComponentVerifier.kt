package io.github.bkmbigo.gallery.processor.internal.verifiers

import io.github.bkmbigo.gallery.ksp.isPrivate
import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.processor.internal.GalleryProcessorException
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ParamWrapper
import io.github.bkmbigo.gallery.processor.internal.utils.*
import io.github.bkmbigo.gallery.processor.internal.utils.isComposable

context(ProcessorEnvironment)
internal fun KSFunctionDeclaration.processComponent(): ComponentWrapper {

    if (!isComposable()) {
        logger.error("@GalleryComponent can only be applied to @Composable functions", this)
        throw GalleryProcessorException()
    }

    if (!isTopLevelFunction()){
        logger.error("@GalleryComponent can only be applied to a top level function")
        throw GalleryProcessorException()
    }

    if (isPrivate()) {
        logger.error("@GalleryComponent cannot be applied to private functions", this)
        throw GalleryProcessorException()
    }

    val paramsWithTypes = parameters.associateWith { it.type.resolve() }

    if (paramsWithTypes.any { (_, types) -> types.isFunctionType || types.isSuspendFunctionType }) {
        logger.error("@GalleryComponent cannot have functions as parameters", this)
        throw GalleryProcessorException()
    }

    if (paramsWithTypes.any { (_, types) -> types.isAny || types.isNothing || types.isUnit }) {
        logger.error("@GalleryComponent cannot have parameters with types Any, Nothing or Unit", this)
        throw GalleryProcessorException()
    }

    if (paramsWithTypes.any { (param, _) -> !param.hasDefault }) {
        logger.error("All parameters in @GalleryComponent must have default values", this)
        throw GalleryProcessorException()
    }

    val params = paramsWithTypes.map { (param, type) ->
        ParamWrapper(
            identifier = null,
            paramName = null,
            name = param.name!!,
            type = type,
            defaultExpression = param.defaultExpression
        )
    }

    return ComponentWrapper(
        componentName = simpleName.getShortName(),
        importList = containingFile?.importDirectives ?: throw GalleryProcessorException(),
        fqName = qualifiedName!!,
        kDoc = docString,
        parameters = params
    )

}
