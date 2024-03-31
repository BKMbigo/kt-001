package io.github.bkmbigo.gallery.processor.internal.verifiers

import io.github.bkmbigo.gallery.ksp.isPrivate
import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.processor.internal.GalleryProcessorException
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ComponentSelectionScreenWrapper
import io.github.bkmbigo.gallery.processor.internal.utils.*
import io.github.bkmbigo.gallery.processor.internal.utils.isComposable

context(ProcessorEnvironment)
internal fun KSFunctionDeclaration.processScreenComponentSelectionScreen(): ComponentSelectionScreenWrapper {

    if (!isComposable()) {
        logger.error("@GalleryScreenComponentSelectionScreen can only be applied to @Composable function", this)
        throw GalleryProcessorException()
    }

    if (isPrivate()) {
        logger.error("@GalleryScreenComponentSelectionScreen can not be applied to private functions", this)
        throw GalleryProcessorException()
    }

    if (typeParameters.isEmpty()) {
        logger.error("@GalleryScreenComponentSelectionScreen must have a generic parameter", this)
        throw GalleryProcessorException()
    } else if (typeParameters.size > 1) {
        logger.error("@GalleryScreenComponentSelectionScreen cannot have more than one generic parameter", this)
        throw GalleryProcessorException()
    } else {
        val typeParam = typeParameters.first()
        val typeParamName = typeParam.simpleName.getShortName()

        val listParam = parameters.filter { param ->
            val type = param.type.resolve()
            (type.isKotlinList || type.isKotlinMutableList || type.isKotlinSet || type.isKotlinMutableSet || type.isKotlinPersistentList) &&
                    param.type.element?.typeArguments?.first()?.type?.resolve()?.declaration?.simpleName?.getShortName() == typeParamName
        }

        if (listParam.isEmpty()) {
            logger.error("@GalleryScreenComponentSelectionScreen must have a list parameter", this)
            throw GalleryProcessorException()
        } else if(listParam.size > 1) {
            logger.error("@GalleryScreenComponentSelectionScreen cannot have more than one list parameter", this)
            throw GalleryProcessorException()
        }

        val listParamName = listParam.first().name!!.getShortName()

        // OnSelection Param

        val onSelectionParam = parameters.filter { param ->
            val type = param.type.resolve()

            type.isFunctionType && !type.annotations.any { it.shortName.getShortName() == "Composable" }
                    && type.declaration.typeParameters.size == 2
                    && param.type.element?.typeArguments?.firstOrNull()?.type?.resolve()?.declaration?.simpleName?.getShortName() == typeParamName
                    && param.type.element?.typeArguments?.getOrNull(1)?.type?.resolve()?.declaration?.simpleName?.getShortName() == "Unit"
        }

        if (onSelectionParam.isEmpty()) {
            logger.error("@GalleryScreenComponentSelectionScreen must have a onSelection parameter", this)
            throw GalleryProcessorException()
        } else if(onSelectionParam.size > 1) {
            logger.error("@GalleryScreenComponentSelectionScreen cannot have more than one onSelection parameter", this)
            throw GalleryProcessorException()
        }

        val onSelectionParamName = onSelectionParam.first().name!!.getShortName()


        return ComponentSelectionScreenWrapper(
            fqName = qualifiedName!!,
            listParam = listParamName,
            listParamIsPersistentList = ComponentSelectionScreenWrapper.ListParamType.List,
            onSelectionParamName = onSelectionParamName,
            path = null
        )
    }

}
