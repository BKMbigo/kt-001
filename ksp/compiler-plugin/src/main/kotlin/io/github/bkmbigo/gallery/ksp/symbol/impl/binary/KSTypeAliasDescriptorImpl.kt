package io.github.bkmbigo.gallery.ksp.symbol.impl.binary

import io.github.bkmbigo.gallery.ksp.common.impl.KSNameImpl
import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.toKSModifiers
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor

class KSTypeAliasDescriptorImpl(descriptor: TypeAliasDescriptor) :
    KSTypeAlias,
    KSDeclarationDescriptorImpl(descriptor),
    KSExpectActual by KSExpectActualDescriptorImpl(descriptor) {
    companion object : KSObjectCache<TypeAliasDescriptor, KSTypeAliasDescriptorImpl>() {
        fun getCached(descriptor: TypeAliasDescriptor) = KSTypeAliasDescriptorImpl.cache.getOrPut(descriptor) {
            KSTypeAliasDescriptorImpl(descriptor)
        }
    }

    override val name: KSName by lazy {
        KSNameImpl.getCached(descriptor.name.asString())
    }

    override val modifiers: Set<Modifier> by lazy {
        descriptor.toKSModifiers()
    }

    override val typeParameters: List<KSTypeParameter> by lazy {
        descriptor.declaredTypeParameters.map { KSTypeParameterDescriptorImpl.getCached(it) }
    }

    override val type: KSTypeReference by lazy {
        KSTypeReferenceDescriptorImpl.getCached(descriptor.underlyingType, origin, this)
    }

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeAlias(this, data)
    }
}
