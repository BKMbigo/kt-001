package com.google.devtools.ksp.common.impl

import com.google.devtools.ksp.common.IdKey
import com.google.devtools.ksp.common.KSObjectCache
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSReferenceElement
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSVisitor
import com.google.devtools.ksp.symbol.Location
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.NonExistLocation
import com.google.devtools.ksp.symbol.Origin

class KSTypeReferenceSyntheticImpl(val ksType: KSType, override val parent: KSNode?) : KSTypeReference {
    companion object : KSObjectCache<Pair<IdKey<KSType>, KSNode?>, KSTypeReferenceSyntheticImpl>() {
        fun getCached(ksType: KSType, parent: KSNode?) = KSTypeReferenceSyntheticImpl.cache
            .getOrPut(Pair(IdKey(ksType), parent)) { KSTypeReferenceSyntheticImpl(ksType, parent) }
    }

    override val annotations: Sequence<KSAnnotation> = emptySequence()

    override val element: KSReferenceElement? = null

    override val location: Location = NonExistLocation

    override val modifiers: Set<Modifier> = emptySet()

    override val origin: Origin = Origin.SYNTHETIC

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeReference(this, data)
    }

    override fun resolve(): KSType {
        return ksType
    }

    override fun toString(): String {
        return ksType.toString()
    }
}
