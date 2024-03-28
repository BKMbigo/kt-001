package io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin

import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.common.impl.KSNameImpl
import io.github.bkmbigo.gallery.ksp.impl.recordLookup
import io.github.bkmbigo.gallery.ksp.impl.recordLookupForGetAllFunctions
import io.github.bkmbigo.gallery.ksp.impl.recordLookupForGetAllProperties
import io.github.bkmbigo.gallery.ksp.symbol.ClassKind
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotation
import io.github.bkmbigo.gallery.ksp.symbol.KSDeclaration
import io.github.bkmbigo.gallery.ksp.symbol.KSExpectActual
import io.github.bkmbigo.gallery.ksp.symbol.KSFile
import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.ksp.symbol.KSName
import io.github.bkmbigo.gallery.ksp.symbol.KSPropertyDeclaration
import io.github.bkmbigo.gallery.ksp.symbol.KSType
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeArgument
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeParameter
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeReference
import org.jetbrains.kotlin.analysis.api.symbols.KtEnumEntrySymbol
import org.jetbrains.kotlin.analysis.api.symbols.KtNamedClassOrObjectSymbol

class KSClassDeclarationEnumEntryImpl private constructor(private val ktEnumEntrySymbol: KtEnumEntrySymbol) :
    io.github.bkmbigo.gallery.ksp.symbol.KSClassDeclaration,
    AbstractKSDeclarationImpl(ktEnumEntrySymbol),
    KSExpectActual by KSExpectActualImpl(ktEnumEntrySymbol) {
    companion object : KSObjectCache<KtEnumEntrySymbol, KSClassDeclarationEnumEntryImpl>() {
        fun getCached(ktEnumEntrySymbol: KtEnumEntrySymbol) =
            cache.getOrPut(ktEnumEntrySymbol) { KSClassDeclarationEnumEntryImpl(ktEnumEntrySymbol) }
    }

    override val qualifiedName: KSName? by lazy {
        KSNameImpl.getCached("${this.parentDeclaration!!.qualifiedName!!.asString()}.${simpleName.asString()}")
    }

    override val classKind: ClassKind = ClassKind.ENUM_ENTRY

    override val primaryConstructor: KSFunctionDeclaration? = null

    override val superTypes: Sequence<KSTypeReference> = emptySequence()

    override val isCompanionObject: Boolean = false

    override fun getSealedSubclasses(): Sequence<io.github.bkmbigo.gallery.ksp.symbol.KSClassDeclaration> {
        TODO("Not yet implemented")
    }

    override fun getAllFunctions(): Sequence<KSFunctionDeclaration> {
        analyze {
            ktEnumEntrySymbol.returnType.getDirectSuperTypes().forEach {
                recordLookup(it, this@KSClassDeclarationEnumEntryImpl)
            }
            recordLookupForGetAllFunctions(ktEnumEntrySymbol.returnType.getDirectSuperTypes())
        }
        return ktEnumEntrySymbol.enumEntryInitializer?.declarations()?.filterIsInstance<KSFunctionDeclaration>()
            ?: emptySequence()
    }

    override fun getAllProperties(): Sequence<KSPropertyDeclaration> {
        analyze {
            ktEnumEntrySymbol.returnType.getDirectSuperTypes().forEach {
                recordLookup(it, this@KSClassDeclarationEnumEntryImpl)
            }
            recordLookupForGetAllProperties(ktEnumEntrySymbol.returnType.getDirectSuperTypes())
        }
        return ktEnumEntrySymbol.enumEntryInitializer?.declarations()?.filterIsInstance<KSPropertyDeclaration>()
            ?: emptySequence()
    }

    override fun asType(typeArguments: List<KSTypeArgument>): KSType {
        return KSTypeImpl.getCached(ktEnumEntrySymbol.returnType).replace(typeArguments)
    }

    override fun asStarProjectedType(): KSType {
        return KSTypeImpl.getCached(ktEnumEntrySymbol.returnType).starProjection()
    }

    override val typeParameters: List<KSTypeParameter> = emptyList()

    override val packageName: KSName by lazy {
        ktEnumEntrySymbol.toContainingFile()!!.packageName
    }

    override val parentDeclaration: KSDeclaration? by lazy {
        analyze {
            (
                ktEnumEntrySymbol.getContainingSymbol()
                    as? KtNamedClassOrObjectSymbol
                )?.let { KSClassDeclarationImpl.getCached(it) }
        }
    }

    override val containingFile: KSFile? by lazy {
        ktEnumEntrySymbol.toContainingFile()
    }

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        ktEnumEntrySymbol.psi.toLocation()
    }

    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode? by lazy {
        analyze {
            (ktEnumEntrySymbol.getContainingSymbol() as? KtNamedClassOrObjectSymbol)
                ?.let { KSClassDeclarationImpl.getCached(it) }
        }
    }

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitClassDeclaration(this, data)
    }

    override val annotations: Sequence<KSAnnotation> = emptySequence()

    override val declarations: Sequence<KSDeclaration> by lazy {
        // TODO: fix after .getDeclaredMemberScope() works for enum entry with no initializer.
        emptySequence()
    }

    override fun defer(): Restorable? {
        return ktEnumEntrySymbol.defer(Companion::getCached)
    }
}
