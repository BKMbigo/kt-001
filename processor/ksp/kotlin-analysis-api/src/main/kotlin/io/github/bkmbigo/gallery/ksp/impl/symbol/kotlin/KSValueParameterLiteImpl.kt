package io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin

import io.github.bkmbigo.gallery.ksp.common.IdKeyPair
import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.common.impl.KSNameImpl
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.resolved.KSTypeReferenceResolvedImpl
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.KSNode
import org.jetbrains.kotlin.analysis.api.types.KtType

class KSValueParameterLiteImpl private constructor(private val ktType: KtType, override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode) :
    KSValueParameter {
    companion object : KSObjectCache<IdKeyPair<KtType, KSNode>, KSValueParameter>() {
        fun getCached(ktType: KtType, parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode): KSValueParameter = cache.getOrPut(
            IdKeyPair(ktType, parent)
        ) {
            KSValueParameterLiteImpl(ktType, parent)
        }
    }

    // preferably maybe use empty name to match compiler, but use underscore to match FE1.0 implementation.
    override val name: KSName = KSNameImpl.getCached("_")

    override val type: KSTypeReference = KSTypeReferenceResolvedImpl.getCached(ktType)

    override val isVararg: Boolean = false

    override val isNoInline: Boolean = false

    override val isCrossInline: Boolean = false

    override val isVal: Boolean = false

    override val isVar: Boolean = false

    override val hasDefault: Boolean = false

    override val defaultExpression: KSExpression? = null

    override val annotations: Sequence<KSAnnotation> = emptySequence()

    override val origin: Origin = parent.origin

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location = parent.location

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitValueParameter(this, data)
    }
}
