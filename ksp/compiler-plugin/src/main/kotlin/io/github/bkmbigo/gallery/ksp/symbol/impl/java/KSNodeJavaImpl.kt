package io.github.bkmbigo.gallery.ksp.symbol.impl.java

import io.github.bkmbigo.gallery.ksp.symbol.impl.toLocation
import com.intellij.psi.PsiElement

abstract class KSNodeJavaImpl(private val psi: PsiElement, override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?) :
    io.github.bkmbigo.gallery.ksp.symbol.KSNode {
    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        psi.toLocation()
    }
}
