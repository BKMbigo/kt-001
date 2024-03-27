package io.github.bkmbigo.gallery.processor.test

import io.github.bkmbigo.gallery.ksp.symbol.*

data class KSFakeTypeImpl(
    private val fqName: String,
    private val isNullable: Boolean = false,
    override val isFunctionType: Boolean = false,
    override val isSuspendFunctionType: Boolean = false,
    override val annotations: Sequence<KSAnnotation> = emptySequence(),
    override val arguments: List<KSTypeArgument> = emptyList()
): KSType {
    override val declaration: KSDeclaration
        get() = KSFakeClassDeclarationImpl(KSNameImpl(fqName))
    override val isError: Boolean
        get() = TODO("Not yet implemented")
    override val isMarkedNullable: Boolean
        get() = isNullable
    override val nullability: Nullability
        get() = TODO("Not yet implemented")

    override fun isAssignableFrom(that: KSType): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCovarianceFlexible(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isMutabilityFlexible(): Boolean {
        TODO("Not yet implemented")
    }

    override fun makeNotNullable(): KSType {
        TODO("Not yet implemented")
    }

    override fun makeNullable(): KSType {
        TODO("Not yet implemented")
    }

    override fun replace(arguments: List<KSTypeArgument>): KSType {
        TODO("Not yet implemented")
    }

    override fun starProjection(): KSType {
        TODO("Not yet implemented")
    }
}
