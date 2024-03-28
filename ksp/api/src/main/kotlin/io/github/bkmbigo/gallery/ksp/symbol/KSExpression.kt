package io.github.bkmbigo.gallery.ksp.symbol

interface KSExpression: KSNode {

    val isIfExpression: Boolean

    val isTryExpression: Boolean

    val isWhenExpression: Boolean

    val isCallExpression: Boolean

    /**
     * Returns true if the expression is a reference
     * */
    val isNameReferenceExpression: Boolean

    val isReferenceExpression: Boolean

    fun getExpressionAsString(): String

}
