package io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin

import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.resolved.KSTypeReferenceResolvedImpl
import io.github.bkmbigo.gallery.ksp.symbol.KSExpectActual
import io.github.bkmbigo.gallery.ksp.symbol.KSName
import io.github.bkmbigo.gallery.ksp.symbol.KSPropertyDeclaration
import io.github.bkmbigo.gallery.ksp.symbol.KSType
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeReference
import org.jetbrains.kotlin.analysis.api.symbols.KtLocalVariableSymbol
import org.jetbrains.kotlin.psi.KtProperty

class KSPropertyDeclarationLocalVariableImpl private constructor(
    private val ktLocalVariableSymbol: KtLocalVariableSymbol
) : KSPropertyDeclaration,
    AbstractKSDeclarationImpl(ktLocalVariableSymbol),
    KSExpectActual by KSExpectActualImpl(ktLocalVariableSymbol) {
    companion object : KSObjectCache<KtLocalVariableSymbol, KSPropertyDeclarationLocalVariableImpl>() {
        fun getCached(ktLocalVariableSymbol: KtLocalVariableSymbol) =
            cache.getOrPut(ktLocalVariableSymbol) { KSPropertyDeclarationLocalVariableImpl(ktLocalVariableSymbol) }
    }

    override val getter: io.github.bkmbigo.gallery.ksp.symbol.KSPropertyGetter? = null

    override val setter: io.github.bkmbigo.gallery.ksp.symbol.KSPropertySetter? = null

    override val extensionReceiver: KSTypeReference? = null

    override val type: KSTypeReference by lazy {
        (ktLocalVariableSymbol.psiIfSource() as? KtProperty)?.typeReference
            ?.let { KSTypeReferenceImpl.getCached(it, this) }
            ?: KSTypeReferenceResolvedImpl.getCached(ktLocalVariableSymbol.returnType, this)
    }

    override val isMutable: Boolean = !ktLocalVariableSymbol.isVal

    override val hasBackingField: Boolean = false

    override fun isDelegated(): Boolean = false

    override fun findOverridee() = null

    override fun asMemberOf(containing: KSType): KSType {
        TODO("Not yet implemented")
    }

    override val qualifiedName: KSName? = null

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitPropertyDeclaration(this, data)
    }

    override fun defer(): Restorable? {
        return ktLocalVariableSymbol.defer(::getCached)
    }
}
