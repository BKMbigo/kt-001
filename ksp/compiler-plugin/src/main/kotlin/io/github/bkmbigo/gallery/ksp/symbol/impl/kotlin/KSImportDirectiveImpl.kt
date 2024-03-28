package io.github.bkmbigo.gallery.ksp.symbol.impl.kotlin

import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.toLocation
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportInfo

class KSImportDirectiveImpl private constructor(private val ktImportDirective: KtImportDirective) : KSImportDirective {
    companion object : KSObjectCache<KtImportDirective, KSImportDirectiveImpl>() {
        fun getCached(ktImportDirective: KtImportDirective) =
            KSImportDirectiveImpl.cache.getOrPut(ktImportDirective) { KSImportDirectiveImpl(ktImportDirective) }
    }

    override val isAllUnder: Boolean
        get() = ktImportDirective.isAllUnder

    override fun hasImported(name: KSName): Boolean =
        when (val importContent = ktImportDirective.importContent) {
            is KtImportInfo.ImportContent.ExpressionBased -> importContent.expression.text == name.getQualifier()
            is KtImportInfo.ImportContent.FqNameBased -> importContent.fqName.asString() == name.asString()
            null -> false
        }

    override fun getImportedReference(): KSExpression? {
        return ktImportDirective.importedReference?.let { KSExpressionKotlinImpl(it) }
    }

    override val origin: Origin = Origin.KOTLIN

    override val location: Location
        get() = ktImportDirective.psiOrParent.toLocation()

    override val parent: KSNode
        get() = KSFileImpl.getCached(ktImportDirective.containingKtFile)

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        TODO("Not yet implemented")
    }
}
