package io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin

import io.github.bkmbigo.gallery.ksp.common.IdKeyPair
import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.*
import org.jetbrains.kotlin.analysis.api.types.KtDefinitelyNotNullType

class KSDefNonNullReferenceImpl private constructor(
    val ktDefinitelyNotNullType: KtDefinitelyNotNullType,
    override val parent: KSTypeReference?
) : KSDefNonNullReference {
    companion object : KSObjectCache<IdKeyPair<KtDefinitelyNotNullType, KSTypeReference?>, KSDefNonNullReference>() {
        fun getCached(ktType: KtDefinitelyNotNullType, parent: KSTypeReference?) =
            KSDefNonNullReferenceImpl.cache
                .getOrPut(IdKeyPair(ktType, parent)) { KSDefNonNullReferenceImpl(ktType, parent) }
    }
    override val enclosedType: KSClassifierReference by lazy {
        ktDefinitelyNotNullType.original.toClassifierReference(parent) as KSClassifierReference
    }
    override val typeArguments: List<KSTypeArgument>
        get() = emptyList()

    override val origin: Origin = Origin.KOTLIN

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location
        get() = parent?.location ?: io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation

    override fun toString() = "${enclosedType.referencedName()} & Any"
}
