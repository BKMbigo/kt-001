package io.github.bkmbigo.gallery.ksp.symbol.impl.kotlin

import io.github.bkmbigo.gallery.ksp.symbol.KSNode
import io.github.bkmbigo.gallery.ksp.symbol.impl.toLocation
import org.jetbrains.kotlin.psi.KtElement

abstract class KSNodeKtImpl(private val element: KtElement) : KSNode {
    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        element.toLocation()
    }
}
