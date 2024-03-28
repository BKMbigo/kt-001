package io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin

import io.github.bkmbigo.gallery.ksp.symbol.KSFunction
import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.ksp.symbol.KSType
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeParameter

class KSFunctionErrorImpl(
    private val declaration: KSFunctionDeclaration
) : io.github.bkmbigo.gallery.ksp.symbol.KSFunction {
    override val isError: Boolean = true

    override val returnType: KSType = KSErrorType

    override val parameterTypes: List<KSType?>
        get() = declaration.parameters.map {
            KSErrorType
        }
    override val typeParameters: List<KSTypeParameter>
        get() = emptyList()

    override val extensionReceiverType: KSType?
        get() = declaration.extensionReceiver?.let {
            KSErrorType
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KSFunctionErrorImpl

        if (declaration != other.declaration) return false

        return true
    }

    override fun hashCode(): Int {
        return declaration.hashCode()
    }
}
