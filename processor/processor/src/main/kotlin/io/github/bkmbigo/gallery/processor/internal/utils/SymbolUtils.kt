package io.github.bkmbigo.gallery.processor.internal.utils

import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import kotlinx.serialization.Serializable

/*
* This file contains types used in saving and loading of stateComponentMap. This types have the ability to be saved and recovered from, say a JSON file.
* However, these types are also very inefficient. They carry alot of unnecessary information.
*
* Since the types are mostly used in preservation between builds... Some functionality is intentionally excluded
* */

@Serializable
internal data class SavedKSType(
    val annotations: List<SavedKSAnnotation>,
    val arguments: List<SavedKSTypeArgument>,
    val declaration: SavedKSDeclaration,
    val isMarkedNullable: Boolean,
    val isFunctionType: Boolean,
    val isSuspendFunctionType: Boolean
) {

    context(ProcessorEnvironment)
    val isUnit
        get() = this == builtIns.unitType.toSavedKSType()

    context(ProcessorEnvironment)
    val isAny
        get() = this == builtIns.anyType.toSavedKSType()

}

internal fun KSType.toSavedKSType(): SavedKSType = SavedKSType(
    annotations = annotations.toList().map { it.toSavedKSAnnotation() },
    arguments = arguments.toList().map { it.toSavedKSTypeArgument() },
    declaration = declaration.toSavedKSDeclaration(),
    isMarkedNullable = isMarkedNullable,
    isFunctionType = isFunctionType,
    isSuspendFunctionType = isSuspendFunctionType
)

internal fun KSDeclaration.toSavedKSDeclaration() = when(this) {
    is KSClassDeclaration -> this.toSavedKSClassDeclaration()
    is KSFunctionDeclaration -> this.toSavedFunctionDeclaration()
    is KSTypeParameter -> this.toSavedKSTypeParameter()
    else -> throw IllegalStateException("Unimplemented Feature")
}

internal fun KSClassDeclaration.toSavedKSClassDeclaration(): SavedKSClassDeclaration = SavedKSClassDeclaration(
    classKind = classKind,
    superTypes = superTypes.toList().map { it.toSavedKSTypeReference() },
    annotations = annotations.toList().map { it.toSavedKSAnnotation() },
    typeParameters = typeParameters.toList().map { it.toSavedKSTypeParameter() },
    modifiers = modifiers,
    containingFile = containingFile?.toSavedKSFile(),
    docString = docString,
    isExpect = isExpect,
    isActual = isActual,
    packageName = packageName,
    qualifiedName = qualifiedName,
    simpleName = simpleName
)

internal fun KSFunctionDeclaration.toSavedFunctionDeclaration(): SavedKSFunctionDeclaration = SavedKSFunctionDeclaration(
    extensionReceiver = extensionReceiver?.toSavedKSTypeReference(),
    functionKind = functionKind,
    returnType = returnType,
    isAbstract = isAbstract,
    parameters = parameters.map { it.toSavedKSTypeParameter() },
    annotations = annotations.toList().map { it.toSavedKSAnnotation() },
    typeParameters = typeParameters.map { it.toSavedKSTypeParameter() },
    modifiers = modifiers,
    containingFile = containingFile?.toSavedKSFile(),
    docString = docString,
    isExpect = isExpect,
    isActual = isActual,
    packageName = packageName,
    qualifiedName = qualifiedName,
    simpleName = simpleName
)

internal interface SavedKSDeclaration {
    val annotations: List<SavedKSAnnotation>
    val typeParameters: List<SavedKSTypeParameter>
    val modifiers: Set<Modifier>
    val containingFile: SavedKSFile?
    val docString: String?
    val isExpect: Boolean
    val isActual: Boolean
    val packageName: KSName
    val qualifiedName: KSName?
    val simpleName: KSName
}

@Serializable
internal data class SavedKSClassDeclaration(
    val classKind: ClassKind,
    val superTypes: List<SavedKSTypeReference>,
    override val annotations: List<SavedKSAnnotation>,
    override val typeParameters: List<SavedKSTypeParameter>,
    override val modifiers: Set<Modifier>,
    override val containingFile: SavedKSFile?,
    override val docString: String?,
    override val isExpect: Boolean,
    override val isActual: Boolean,
    override val packageName: KSName,
    override val qualifiedName: KSName?,
    override val simpleName: KSName
): SavedKSDeclaration

@Serializable
internal data class SavedKSFunctionDeclaration(
    val extensionReceiver: SavedKSTypeReference?,
    val functionKind: FunctionKind,
    val returnType: KSTypeReference?,
    val isAbstract: Boolean,
    val parameters: List<SavedKSValueParameter>,
    override val annotations: List<SavedKSAnnotation>,
    override val typeParameters: List<SavedKSTypeParameter>,
    override val modifiers: Set<Modifier>,
    override val containingFile: SavedKSFile?,
    override val docString: String?,
    override val isExpect: Boolean,
    override val isActual: Boolean,
    override val packageName: KSName,
    override val qualifiedName: KSName?,
    override val simpleName: KSName
): SavedKSDeclaration

internal fun KSAnnotation.toSavedKSAnnotation() = SavedKSAnnotation(
    shortName = shortName,
    useSiteTarget = useSiteTarget,
    annotationType = annotationType.toSavedKSTypeReference()
)

@Serializable
internal data class SavedKSAnnotation(
    val shortName: KSName,
    val useSiteTarget: AnnotationUseSiteTarget?,
    val annotationType: SavedKSTypeReference,
    // arguments
    // defaultArguments
)

internal fun KSTypeReference.toSavedKSTypeReference(): SavedKSTypeReference = SavedKSTypeReference(
    annotations = annotations.toList().map { it.toSavedKSAnnotation() },
    element = element?.toSavedKSReferenceElement(),
    modifiers = modifiers
)

@Serializable
internal data class SavedKSTypeReference(
    val annotations: List<SavedKSAnnotation>,
    val element: SavedKSReferenceElement?,
    val modifiers: Set<Modifier>
)

internal fun KSReferenceElement.toSavedKSReferenceElement() = SavedKSReferenceElement(
    typeArguments = typeArguments.map { it.toSavedKSTypeArgument() }
)

@Serializable
internal data class SavedKSReferenceElement(
    val typeArguments: List<SavedKSTypeArgument>
)

internal fun KSTypeArgument.toSavedKSTypeArgument() = SavedKSTypeArgument(
    annotations = annotations.toList().map { it.toSavedKSAnnotation() },
    type = type?.toSavedKSTypeReference()
)

@Serializable
internal data class SavedKSTypeArgument(
    val annotations: List<SavedKSAnnotation>,
    val type: SavedKSTypeReference?
)

internal fun KSValueParameter.toSavedKSTypeParameter() = SavedKSValueParameter(
    annotations = annotations.toList().map { it.toSavedKSAnnotation() },
    hasDefault = hasDefault,
    type = type.toSavedKSTypeReference(),
    name = name
)

@Serializable
internal data class SavedKSValueParameter(
    val annotations: List<SavedKSAnnotation>,
    val hasDefault: Boolean,
    val type: SavedKSTypeReference,
    val name: KSName?
)

internal fun KSTypeParameter.toSavedKSTypeParameter(): SavedKSTypeParameter = SavedKSTypeParameter(
    annotations = annotations.toList().map { it.toSavedKSAnnotation() },
    docString = docString,
    name = name,
    packageName = packageName,
    qualifiedName = qualifiedName,
    simpleName = simpleName,
    typeParameters = typeParameters.map { it.toSavedKSTypeParameter() },
    modifiers = modifiers,
    containingFile = containingFile?.toSavedKSFile(),
    isActual = isActual,
    isExpect = isExpect
)

@Serializable
internal data class SavedKSTypeParameter(
    val name: KSName,
    override val annotations: List<SavedKSAnnotation>,
    override val containingFile: SavedKSFile?,
    override val docString: String?,
    override val isExpect: Boolean,
    override val isActual: Boolean,
    override val packageName: KSName,
    override val qualifiedName: KSName?,
    override val simpleName: KSName,
    override val typeParameters: List<SavedKSTypeParameter>,
    override val modifiers: Set<Modifier>
): SavedKSDeclaration

internal fun KSFile.toSavedKSFile() = SavedKSFile(
    fileName = fileName,
    filePath = filePath,
    packageName = packageName
)

@Serializable
internal data class SavedKSFile(
    val fileName: String,
    val filePath: String,
    val packageName: KSName
)
