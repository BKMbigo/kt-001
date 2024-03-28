package io.github.bkmbigo.gallery.ksp.symbol.impl.kotlin

import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.toLocation
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isFunctionalExpression

class KSExpressionKotlinImpl(
    private val ktExpression: KtExpression
): KSExpression {

    override val isIfExpression: Boolean
        get() = ktExpression is KtIfExpression

    override val isTryExpression: Boolean
        get() = ktExpression is KtTryExpression

    override val isWhenExpression: Boolean
        get() = ktExpression is KtWhenExpression

    override val isCallExpression: Boolean
        get() = ktExpression is KtCallExpression

    override val isNameReferenceExpression: Boolean
        get() = ktExpression is KtNameReferenceExpression

    override val isReferenceExpression: Boolean
        get() = ktExpression is KtReferenceExpression

    override fun getExpressionAsString(): String =
        ktExpression.text

    override val origin: Origin = Origin.KOTLIN

    override val location: Location by lazy {
        ktExpression.toLocation()
    }

    override val parent: KSNode? = null

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        /* no-op */
        return visitor.visitNode(this, data)
    }
}
