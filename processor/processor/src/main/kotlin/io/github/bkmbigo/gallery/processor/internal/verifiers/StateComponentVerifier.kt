package io.github.bkmbigo.gallery.processor.internal.verifiers

import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.processor.internal.Constants
import io.github.bkmbigo.gallery.processor.internal.GalleryProcessorException
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.StateComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.utils.isComposable
import io.github.bkmbigo.gallery.processor.internal.utils.isString
import io.github.bkmbigo.gallery.processor.internal.utils.isTopLevelFunction
import io.github.bkmbigo.gallery.processor.internal.utils.isUnit

context(ProcessorEnvironment)
internal fun KSFunctionDeclaration.processStateComponent(): StateComponentWrapper {

    if (!isComposable()) {
        logger.error("@GalleryStateComponent can only be applied to @Composable functions", this)
        throw GalleryProcessorException()
    }

    /*
    * @GalleryStateComponent<Int>
    * fun IntComponent(
    *   paramName: String, // Optional parameter
    *   state: Int,
    *   onState: (Int) -> Unit
    * ) {
    * }
    * */

    /*
    * Check for state parameter (a parameter that takes the generic type <T>)
    * Check for onState parameter (a parameter that takes a function (T) -> Unit)
    * */

    val galleryStateRowAnnotations = annotations.filter { it.shortName.getShortName() == Constants.Annotations.SimpleName.GalleryStateRow }.toList()
    val galleryStatePageAnnotations = annotations.filter { it.shortName.getShortName() == Constants.Annotations.SimpleName.GalleryStatePage }.toList()

    if (galleryStatePageAnnotations.isNotEmpty() && galleryStateRowAnnotations.isNotEmpty()) {
        logger.error("@GalleryStateComponent cannot simultaneously a row and a page", this)
    } else if (galleryStatePageAnnotations.isEmpty() && galleryStateRowAnnotations.isEmpty()) {
        logger.error("Error processing annotations at component", this)
    }

    val galleryStateComponentAnnotations = galleryStateRowAnnotations + galleryStatePageAnnotations

    if (galleryStateComponentAnnotations.size > 1) {
        logger.error("Currently, @GalleryStateComponent(s) can only have a single annotation", this)
    }

    val stateComponentAnnotation = galleryStateComponentAnnotations.first()
    val stateComponentType = stateComponentAnnotation.annotationType.element?.typeArguments?.firstOrNull()?.type?.resolve()

    val paramTypes = parameters.map { it to it.type.resolve() }

    // Search for 'state' parameters
    val stateParameters = paramTypes.filter { (param, type) ->
        !type.isFunctionType
                && !type.isSuspendFunctionType
                && type == stateComponentType
                // In case of a String StateComponent, ignore `paramName` parameter
                && (param.name?.getShortName() != "paramName")
    }

    if (stateParameters.isEmpty()) {
        logger.error("@GalleryStateComponent<${stateComponentType?.declaration?.qualifiedName?.getShortName()}> needs to have a parameter of type ${stateComponentType?.declaration?.qualifiedName?.getShortName()}", this)
    } else if(stateParameters.size > 1) {
        logger.error("@GalleryStateComponent<${stateComponentType?.declaration?.qualifiedName?.getShortName()}> cannot have multiple parameters of type ${stateComponentType?.declaration?.qualifiedName?.getShortName()}", this)
    }

    // Search for `onState` parameters
    val onStateParameters = paramTypes.filter { (valueParam, type) ->
        type.isFunctionType && type.arguments.getOrNull(0)?.type?.resolve() == stateComponentType && valueParam.type.element?.typeArguments?.getOrNull(1)?.type?.resolve()?.isUnit == true
    }

    if (onStateParameters.isEmpty()) {
        logger.error("@GalleryStateComponent<${stateComponentType?.declaration?.qualifiedName?.getShortName()}> needs to have a parameter of type (${stateComponentType?.declaration?.qualifiedName?.getShortName()}) -> Unit", this)
    } else if(onStateParameters.size > 1) {
        logger.error("@GalleryStateComponent<${stateComponentType?.declaration?.qualifiedName?.getShortName()}> cannot have multiple parameters of type (${stateComponentType?.declaration?.qualifiedName?.getShortName()}) -> Unit", this)
    }

    // search for parameter with name `paramName`
    val hasParamNameParameter: Boolean
    val paramNameParameter = parameters.firstOrNull { it.name?.getShortName() == "paramName" }

    if (paramNameParameter != null) {
        if (!paramNameParameter.type.resolve().isString) {
            logger.error("@GalleryStateComponent cannot have a parameter `paramName` that does not have the type kotlin.String", this)
            throw GalleryProcessorException()
        }

        hasParamNameParameter = true
    } else {
        hasParamNameParameter = false
    }


    // search for parameter with name `onNavigateBack`
    val hasOnNavigateBackNameParameter: Boolean
    val onNavigateBackParameter = parameters.firstOrNull { it.name?.getShortName() == "onNavigateBack" }

    if (onNavigateBackParameter != null) {
        val onNavigateBackType = onNavigateBackParameter.type.resolve()

        if (!onNavigateBackType.isFunctionType) {
            logger.error(
                "@GalleryStateComponent cannot have a parameter `onNavigateBack` that is not the type (() -> Unit). Is not a function",
                this
            )
            throw GalleryProcessorException()
        } else if (onNavigateBackType.annotations.any { it.shortName.getShortName() == "Composable" }) {
            logger.error(
                "@GalleryStateComponent cannot have a parameter `onNavigateBack` that is takes a @Composable lambda",
                this
            )
            throw GalleryProcessorException()
        } else if (onNavigateBackParameter.type.element?.typeArguments?.size != 1) {
            logger.error(
                "@GalleryStateComponent cannot have a parameter `onNavigateBack` that is not the type (() -> Unit). Type arguments error",
                this
            )
            throw GalleryProcessorException()
        } else if (onNavigateBackParameter.type.element?.typeArguments?.getOrNull(0)?.type?.resolve()?.isUnit != true) {
            logger.error(
                "@GalleryStateComponent cannot have a parameter `onNavigateBack` that is not the type (() -> Unit). Does not return kotlin.Unit",
                this
            )
            throw GalleryProcessorException()
        }

        hasOnNavigateBackNameParameter = true
    } else {
        hasOnNavigateBackNameParameter = false
    }




    // All other parameters should have a default value
    val otherParameters = (parameters - stateParameters.map { it.first }.toSet() - onStateParameters.map { it.first }.toSet()).toMutableSet()
    if (hasParamNameParameter) {
        otherParameters.remove(paramNameParameter)
        otherParameters.remove(onNavigateBackParameter)
    }

    if (otherParameters.any { param -> !param.hasDefault }) {
        logger.error("@GalleryStateComponent has a unrecognized non-default parameter", this)
        throw GalleryProcessorException()
    }

    return StateComponentWrapper(
        isRow = galleryStateRowAnnotations.isNotEmpty(),
        fqName = this.qualifiedName!!,
        type = stateComponentType!!,
        isDefault = true,
        identifier = null,
        stateParameterName = stateParameters.first().first.name!!.getShortName(),
        onStateParameterName = onStateParameters.first().first.name!!.getShortName(),
        paramNameParameterName = if (hasParamNameParameter) "paramName" else null,
        onNavigateBackParameterName = if (hasOnNavigateBackNameParameter) "onNavigateBack" else null,
    )

}
