package io.github.bkmbigo.gallery.ksp.symbol.impl.synthetic

import io.github.bkmbigo.gallery.ksp.ExceptionMessage
import io.github.bkmbigo.gallery.ksp.common.impl.KSNameImpl
import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.processing.impl.findAnnotationFromUseSiteTarget
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.binary.KSAnnotationDescriptorImpl
import io.github.bkmbigo.gallery.ksp.symbol.impl.binary.KSTypeReferenceDescriptorImpl
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.resolve.calls.components.hasDefaultValue
import org.jetbrains.kotlin.resolve.calls.components.isVararg

class KSValueParameterSyntheticImpl(val owner: KSAnnotated?, resolve: () -> ValueParameterDescriptor?) :
    KSValueParameter {

    companion object :
        KSObjectCache<Pair<KSAnnotated?, () -> ValueParameterDescriptor?>, KSValueParameterSyntheticImpl>() {
        fun getCached(owner: KSAnnotated? = null, resolve: () -> ValueParameterDescriptor?) =
            KSValueParameterSyntheticImpl.cache.getOrPut(Pair(owner, resolve)) {
                KSValueParameterSyntheticImpl(owner, resolve)
            }
    }

    private val descriptor by lazy {
        resolve() ?: throw IllegalStateException("Failed to resolve for synthetic value parameter, $ExceptionMessage")
    }

    override val name: KSName? by lazy {
        KSNameImpl.getCached(descriptor.name.asString())
    }

    override val type: KSTypeReference by lazy {
        KSTypeReferenceDescriptorImpl.getCached(descriptor.type, origin, this)
    }

    override val isVararg: Boolean = descriptor.isVararg

    override val isNoInline: Boolean = descriptor.isNoinline

    override val isCrossInline: Boolean = descriptor.isCrossinline

    override val isVal: Boolean = !descriptor.isVar

    override val isVar: Boolean = descriptor.isVar

    override val hasDefault: Boolean = descriptor.hasDefaultValue()

    override val defaultExpression: KSExpression? = throw IllegalStateException("Not implemented in KSP1 synthetic")

    override val annotations: Sequence<KSAnnotation> by lazy {
        descriptor.annotations.asSequence()
            .map { KSAnnotationDescriptorImpl.getCached(it, this) }.plus(this.findAnnotationFromUseSiteTarget())
    }

    override val origin: Origin = Origin.SYNTHETIC

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location =
        io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation

    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode? = owner

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitValueParameter(this, data)
    }

    override fun toString(): String {
        return name?.asString() ?: "_"
    }
}
