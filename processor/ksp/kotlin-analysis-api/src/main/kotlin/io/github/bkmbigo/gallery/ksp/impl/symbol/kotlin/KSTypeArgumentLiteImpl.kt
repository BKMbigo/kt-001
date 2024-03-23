package io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin

import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotation
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeArgument
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeReference
import io.github.bkmbigo.gallery.ksp.symbol.Origin
import io.github.bkmbigo.gallery.ksp.symbol.Variance

class KSTypeArgumentLiteImpl private constructor(override val type: KSTypeReference, override val variance: Variance) :
    KSTypeArgument, Deferrable {
    companion object : KSObjectCache<Pair<KSTypeReference, Variance>, KSTypeArgument>() {
        fun getCached(type: KSTypeReference, variance: Variance) = cache.getOrPut(Pair(type, variance)) {
            KSTypeArgumentLiteImpl(type, variance)
        }
    }

    override val annotations: Sequence<KSAnnotation> = emptySequence()

    override val origin: Origin = Origin.SYNTHETIC

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location =
        io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation

    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode? = null

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeArgument(this, data)
    }

    override fun defer(): Restorable? {
        TODO("Not yet implemented")
    }
}
