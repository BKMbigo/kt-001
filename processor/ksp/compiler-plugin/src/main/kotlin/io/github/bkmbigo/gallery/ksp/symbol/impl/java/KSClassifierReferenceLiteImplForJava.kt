package io.github.bkmbigo.gallery.ksp.symbol.impl.java

import io.github.bkmbigo.gallery.ksp.ExceptionMessage
import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.KSClassifierReference
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeArgument
import io.github.bkmbigo.gallery.ksp.symbol.Origin
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiMethod

class KSClassifierReferenceLiteImplForJava(
    override val parent: KSTypeReferenceLiteJavaImpl,
    private val name: String?
) : KSClassifierReference {
    companion object : KSObjectCache<Pair<KSTypeReferenceLiteJavaImpl, String?>, KSClassifierReference>() {
        fun getCached(parent: KSTypeReferenceLiteJavaImpl, name: String? = null) =
            KSClassifierReferenceLiteImplForJava.cache
                .getOrPut(Pair(parent, name)) { KSClassifierReferenceLiteImplForJava(parent, name) }
    }
    override val qualifier: KSClassifierReference? by lazy {
        val referencedName = referencedName()
        if (referencedName.lastIndexOf('.') == -1) {
            null
        } else {
            getCached(parent, referencedName.substringBeforeLast('.'))
        }
    }

    override fun referencedName(): String {
        return name ?: when (parent.psiElement) {
            is PsiAnnotation -> parent.psiElement.nameReferenceElement?.text ?: "<ERROR>"
            is PsiMethod -> parent.psiElement.name
            else -> throw IllegalStateException(
                "Unexpected psi type in KSTypeReferenceLiteJavaImpl: ${parent.psiElement.javaClass}, $ExceptionMessage"
            )
        }
    }

    override val typeArguments: List<KSTypeArgument> = emptyList()

    override val origin: Origin = Origin.JAVA

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location =
        io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation

    override fun toString(): String {
        return referencedName()
    }
}
