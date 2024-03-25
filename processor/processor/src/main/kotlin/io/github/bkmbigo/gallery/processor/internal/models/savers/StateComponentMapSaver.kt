package io.github.bkmbigo.gallery.processor.internal.models.savers

import io.github.bkmbigo.gallery.processor.internal.models.savers.symbols.SavedKotlinType
import kotlinx.serialization.Serializable

@Serializable
internal data class StateComponentMapSaver(
    val stateMap: Map<SavedKotlinType, StateComponentTypeListSaver> = emptyMap(),

    /*
    * Unresolvable types are not cached through builds
    *   This is because it will increase the workload of updating the list when a new @GalleryStateComponent is added.(It will be expensive to check if any of the unresolvable types is a descendant of a new Type)
    *  */
//    val unresolvableTypes: List<String> = emptyList(),

    /*
    * This list can potentially be updated according to the following principle:
    * Class A
    * Class B:A
    * Class C:B
    * Class D:C
    *   If there exists a @GalleryStateComponent<A>, the quickJumpMap will contain a pair (D to A)
    *
    *   When a new @GalleryStateComponent<B || C || D> is introduced, we can check if A is a supertype of the second element in a pair in the quickJumpMap. This would however fail if the resolved jumpTypeDestination is not a direct supertype i.e when a type has more than one supertype
    *
    * */
//    val quickJumpMap: Map<String, String> = emptyMap()
)
