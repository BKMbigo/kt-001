package io.github.bkmbigo.gallery.processor.internal.models.savers.symbols

import io.github.bkmbigo.gallery.ksp.symbol.KSType
import kotlinx.serialization.Serializable

/*
* This type is used in [StateComponentMap] to save and retrieve types in the map.
*  */
@Serializable
internal data class SavedKotlinType( // List<>
    val fqName: String, // kotlin.collections.List
    val isNullable: Boolean // false
)

/**
 * Converts the type to a saveable type
 * */
internal fun KSType.toSavedKotlinType(): SavedKotlinType =
    SavedKotlinType(
        fqName = declaration.qualifiedName?.asString() ?: declaration.simpleName.asString(),
        isNullable = isMarkedNullable
    )
