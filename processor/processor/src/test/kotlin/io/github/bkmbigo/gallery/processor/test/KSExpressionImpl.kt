package io.github.bkmbigo.gallery.processor.test

import io.github.bkmbigo.gallery.ksp.symbol.*
import org.intellij.lang.annotations.Language

data class KSExpressionImpl(
    private val expression: String
): KSExpression {
    override val isCallExpression: Boolean
        get() = TODO("Not yet implemented")
    override val isIfExpression: Boolean
        get() = TODO("Not yet implemented")
    override val isNameReferenceExpression: Boolean
        get() = TODO("Not yet implemented")
    override val isReferenceExpression: Boolean
        get() = TODO("Not yet implemented")
    override val isTryExpression: Boolean
        get() = TODO("Not yet implemented")
    override val isWhenExpression: Boolean
        get() = TODO("Not yet implemented")
    override val location: Location
        get() = TODO("Not yet implemented")
    override val origin: Origin
        get() = TODO("Not yet implemented")
    override val parent: KSNode?
        get() = TODO("Not yet implemented")

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        TODO("Not yet implemented")
    }

    override fun getExpressionAsString(): String = expression
}
