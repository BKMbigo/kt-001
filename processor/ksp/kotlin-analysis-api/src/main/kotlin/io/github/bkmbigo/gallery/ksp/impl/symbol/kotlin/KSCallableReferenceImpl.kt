package io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin

import io.github.bkmbigo.gallery.ksp.common.IdKeyPair
import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.resolved.KSTypeArgumentResolvedImpl
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.resolved.KSTypeReferenceResolvedImpl
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.KSNode
import org.jetbrains.kotlin.analysis.api.types.KtFunctionalType
import org.jetbrains.kotlin.analysis.api.types.KtType

// TODO: implement a psi based version, rename this class to resolved Impl.
class KSCallableReferenceImpl private constructor(
    private val ktFunctionalType: KtFunctionalType,
    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?
) : KSCallableReference {
    companion object : KSObjectCache<IdKeyPair<KtType, KSNode?>, KSCallableReference>() {
        fun getCached(ktFunctionalType: KtFunctionalType, parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?): KSCallableReference =
            cache.getOrPut(IdKeyPair(ktFunctionalType, parent)) { KSCallableReferenceImpl(ktFunctionalType, parent) }
    }
    override val receiverType: KSTypeReference?
        get() = ktFunctionalType.receiverType?.let { KSTypeReferenceResolvedImpl.getCached(it) }

    override val functionParameters: List<KSValueParameter>
        get() = ktFunctionalType.parameterTypes.map {
            KSValueParameterLiteImpl.getCached(it, this@KSCallableReferenceImpl)
        }

    override val returnType: KSTypeReference
        get() = KSTypeReferenceResolvedImpl.getCached(ktFunctionalType.returnType)

    override val typeArguments: List<KSTypeArgument>
        get() = ktFunctionalType.typeArguments().map { KSTypeArgumentResolvedImpl.getCached(it, this) }

    override val origin: Origin
        get() = parent?.origin ?: Origin.SYNTHETIC

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location
        get() = parent?.location ?: io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation
}
