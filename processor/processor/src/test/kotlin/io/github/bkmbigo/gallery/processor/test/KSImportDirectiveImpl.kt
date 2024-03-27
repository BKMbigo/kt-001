package io.github.bkmbigo.gallery.processor.test

import io.github.bkmbigo.gallery.ksp.symbol.*

class KSImportDirectiveImpl(private val importDefinition: String): KSImportDirective {
    override val isAllUnder: Boolean
        get() = importDefinition.substringAfterLast(".") == "*"

    override fun getImportedReference(): KSExpression =
        if (isAllUnder) {
            KSExpressionImpl(importDefinition.substringBeforeLast("."))
        } else {
            KSExpressionImpl(importDefinition)
        }


    override fun hasImported(name: KSName): Boolean =
        importDefinition.substringBeforeLast(".") == name.getQualifier()

    override val location: Location
        get() = TODO("Not implemented in Test")
    override val origin: Origin
        get() = TODO("Not implemented in Test")
    override val parent: KSNode?
        get() = TODO("Not implemented in Test")

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        TODO("Not implemented in Test")
    }
}
