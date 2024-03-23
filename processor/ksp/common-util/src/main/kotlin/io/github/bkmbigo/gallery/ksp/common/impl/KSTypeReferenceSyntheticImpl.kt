package io.github.bkmbigo.gallery.ksp.common.impl

import io.github.bkmbigo.gallery.ksp.common.IdKey
import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotation
import io.github.bkmbigo.gallery.ksp.symbol.KSNode
import io.github.bkmbigo.gallery.ksp.symbol.KSReferenceElement
import io.github.bkmbigo.gallery.ksp.symbol.KSType
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeReference
import io.github.bkmbigo.gallery.ksp.symbol.Modifier
import io.github.bkmbigo.gallery.ksp.symbol.Origin

class KSTypeReferenceSyntheticImpl(val ksType: KSType, override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?) : KSTypeReference {
    companion object : KSObjectCache<Pair<IdKey<KSType>, KSNode?>, KSTypeReferenceSyntheticImpl>() {
        fun getCached(ksType: KSType, parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?) = KSTypeReferenceSyntheticImpl.cache
            .getOrPut(Pair(IdKey(ksType), parent)) { KSTypeReferenceSyntheticImpl(ksType, parent) }
    }

    override val annotations: Sequence<KSAnnotation> = emptySequence()

    override val element: KSReferenceElement? = null

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location =
        io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation

    override val modifiers: Set<Modifier> = emptySet()

    override val origin: Origin = Origin.SYNTHETIC

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeReference(this, data)
    }

    override fun resolve(): KSType {
        return ksType
    }

    override fun toString(): String {
        return ksType.toString()
    }
}
