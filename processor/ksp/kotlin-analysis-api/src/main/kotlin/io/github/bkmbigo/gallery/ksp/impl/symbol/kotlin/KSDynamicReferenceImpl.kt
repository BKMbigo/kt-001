package io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin

import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.*

class KSDynamicReferenceImpl private constructor(override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode) : KSDynamicReference {
    companion object : KSObjectCache<KSTypeReference, KSDynamicReferenceImpl>() {
        fun getCached(parent: KSTypeReference) = cache.getOrPut(parent) { KSDynamicReferenceImpl(parent) }
    }

    override val origin = Origin.KOTLIN

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation
    }

    override val typeArguments: List<KSTypeArgument> = emptyList()

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitDynamicReference(this, data)
    }

    override fun toString(): String {
        return "<dynamic type>"
    }
}
