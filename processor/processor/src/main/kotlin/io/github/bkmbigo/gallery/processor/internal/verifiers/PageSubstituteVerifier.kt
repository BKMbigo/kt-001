package io.github.bkmbigo.gallery.processor.internal.verifiers

import io.github.bkmbigo.gallery.ksp.isPrivate
import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.processor.internal.Constants
import io.github.bkmbigo.gallery.processor.internal.GalleryProcessorException
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.PageSubstituteWrapper
import io.github.bkmbigo.gallery.processor.internal.utils.isComposable
import io.github.bkmbigo.gallery.processor.internal.utils.isString


context(ProcessorEnvironment)
internal fun KSFunctionDeclaration.processPageSubstitute(): PageSubstituteWrapper {

    if (!isComposable()) {
        logger.error("@GalleryPageSubsitute can only be used on a @Composable function", this)
        throw GalleryProcessorException()
    }

    if (isPrivate()) {
        logger.error("@GalleryPageSubsitute can only be used on a private function", this)
        throw GalleryProcessorException()
    }

    /*
    * As for parameters:
    *       paramName [Optional]: kotlin.String [Resolved by name]
    *       onNavigateToScreen [Required]: () -> Unit [Resolved by name]
    * */

    val pageSubstituteAnnotation = annotations.firstOrNull { it.shortName.getShortName() == Constants.Annotations.SimpleName.GalleryPageSubstitute } ?: throw GalleryProcessorException()
    val pageAnnotationType = pageSubstituteAnnotation.annotationType.element?.typeArguments?.firstOrNull()?.type?.resolve() ?: throw GalleryProcessorException()

    val hasParamNameParameter: Boolean
    val paramNameParameter = parameters.firstOrNull { it.name?.getShortName() == "paramName" }
    if (paramNameParameter != null) {

        if(!paramNameParameter.type.resolve().isString) {
            logger.error("@GalleryPageSubstitute cannot have a parameter `paramName` that is not of type kotlin.String", this)
            throw GalleryProcessorException()
        }

        hasParamNameParameter = true
    } else {
        hasParamNameParameter = false
    }

    return PageSubstituteWrapper(
        fqName = qualifiedName!!,
        type = pageAnnotationType,
        paramNameParameterName = if (hasParamNameParameter) "paramName" else null,
        onNavigateToScreenParameterName = "onNavigateToScreen"
    )
}
