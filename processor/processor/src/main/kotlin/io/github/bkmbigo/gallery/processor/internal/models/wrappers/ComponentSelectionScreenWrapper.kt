package io.github.bkmbigo.gallery.processor.internal.models.wrappers

import io.github.bkmbigo.gallery.ksp.symbol.KSName

/*
* @ScreenComponentSelectionScreen
* @Composable
* fun <T> SelectionScreen(
*   onComponentSelected: (T) -> Unit,
*   components: List<T>,
* ) { ... }
* */
internal data class ComponentSelectionScreenWrapper(
    val fqName: KSName,
    val listParam: String,
    val listParamIsPersistentList: ListParamType,
    val onSelectionParamName: String,
    val path: String? = null
) {

    enum class ListParamType {
        Set,
        List,
        PersistentList
    }
}
