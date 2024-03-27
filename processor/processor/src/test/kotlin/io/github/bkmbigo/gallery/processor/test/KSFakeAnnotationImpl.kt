package io.github.bkmbigo.gallery.processor.test

import io.github.bkmbigo.gallery.ksp.symbol.*

data class KSFakeAnnotationImpl(
    override val shortName: KSName,
): KSAnnotation {
    override val annotationType: KSTypeReference
        get() = TODO("Not yet implemented")
    override val arguments: List<KSValueArgument>
        get() = TODO("Not yet implemented")
    override val defaultArguments: List<KSValueArgument>
        get() = TODO("Not yet implemented")
    override val location: Location
        get() = TODO("Not yet implemented")
    override val origin: Origin
        get() = TODO("Not yet implemented")
    override val parent: KSNode?
        get() = TODO("Not yet implemented")
    override val useSiteTarget: AnnotationUseSiteTarget?
        get() = TODO("Not yet implemented")

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        TODO("Not yet implemented")
    }
}
