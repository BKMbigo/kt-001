package io.github.bkmbigo.gallery.ksp.symbol.impl.kotlin

import io.github.bkmbigo.gallery.ksp.common.findParentOfType
import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.toLocation
import org.jetbrains.kotlin.psi.KtIntersectionType
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType

class KSDefNonNullReferenceImpl private constructor(val ktIntersectionType: KtIntersectionType) :
    KSDefNonNullReference {
    companion object : KSObjectCache<KtIntersectionType, KSDefNonNullReferenceImpl>() {
        fun getCached(ktIntersectionType: KtIntersectionType) = KSDefNonNullReferenceImpl
            .cache.getOrPut(ktIntersectionType) { KSDefNonNullReferenceImpl(ktIntersectionType) }
    }

    override val enclosedType: KSClassifierReference by lazy {
        val lhs = ktIntersectionType.getLeftTypeRef()?.typeElement
        if (lhs is KtUserType) {
            KSClassifierReferenceImpl.getCached(lhs)
        } else {
            throw IllegalStateException("LHS operand of definitely non null type should be a user type")
        }
    }

    override val typeArguments: List<KSTypeArgument>
        get() = emptyList()

    override val origin: Origin
        get() = Origin.KOTLIN

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location
        get() = ktIntersectionType.toLocation()

    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode? by lazy {
        ktIntersectionType.findParentOfType<KtTypeReference>()?.let { KSTypeReferenceImpl.getCached(it) }
    }

    override fun toString() = "${enclosedType.referencedName()} & Any"
}
