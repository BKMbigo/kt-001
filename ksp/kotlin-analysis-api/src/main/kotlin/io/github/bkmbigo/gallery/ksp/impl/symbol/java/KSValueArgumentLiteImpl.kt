package io.github.bkmbigo.gallery.ksp.impl.symbol.java

import io.github.bkmbigo.gallery.ksp.common.IdKeyPair
import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotation
import io.github.bkmbigo.gallery.ksp.symbol.KSName
import io.github.bkmbigo.gallery.ksp.symbol.KSValueArgument
import io.github.bkmbigo.gallery.ksp.symbol.Origin

class KSValueArgumentLiteImpl private constructor(
    override val name: KSName?,
    override val value: Any?,
    override val origin: Origin
) : KSValueArgument {
    companion object : KSObjectCache<IdKeyPair<KSName?, Any?>, KSValueArgumentLiteImpl>() {
        fun getCached(name: KSName?, value: Any?, origin: Origin) =
            KSValueArgumentLiteImpl.cache
                .getOrPut(IdKeyPair(name, value)) { KSValueArgumentLiteImpl(name, value, origin) }
    }
    override val isSpread: Boolean = false

    override val annotations: Sequence<KSAnnotation> = emptySequence()

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location
        get() = TODO("Not yet implemented")

    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?
        get() = TODO("Not yet implemented")

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitValueArgument(this, data)
    }
}
