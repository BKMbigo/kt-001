package io.github.bkmbigo.gallery.ksp.symbol

interface KSImportDirective: KSNode {

    val isAllUnder: Boolean

    fun hasImported(name: KSName): Boolean

    fun getImportedReference(): KSExpression?

}
