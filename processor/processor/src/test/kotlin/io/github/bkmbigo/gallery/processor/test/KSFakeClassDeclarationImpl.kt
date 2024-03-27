package io.github.bkmbigo.gallery.processor.test

import io.github.bkmbigo.gallery.ksp.symbol.*

class KSFakeClassDeclarationImpl(
    private val fqName: KSName,
): KSClassDeclaration {
    override val annotations: Sequence<KSAnnotation>
        get() = TODO("Not yet implemented")
    override val classKind: ClassKind
        get() = TODO("Not yet implemented")
    override val containingFile: KSFile?
        get() = TODO("Not yet implemented")
    override val declarations: Sequence<KSDeclaration>
        get() = TODO("Not yet implemented")
    override val docString: String?
        get() = TODO("Not yet implemented")
    override val isActual: Boolean
        get() = TODO("Not yet implemented")
    override val isCompanionObject: Boolean
        get() = TODO("Not yet implemented")
    override val isExpect: Boolean
        get() = TODO("Not yet implemented")
    override val location: Location
        get() = TODO("Not yet implemented")
    override val modifiers: Set<Modifier>
        get() = TODO("Not yet implemented")
    override val origin: Origin
        get() = TODO("Not yet implemented")
    override val packageName: KSName
        get() = TODO("Not yet implemented")
    override val parent: KSNode?
        get() = TODO("Not yet implemented")
    override val parentDeclaration: KSDeclaration?
        get() = TODO("Not yet implemented")
    override val primaryConstructor: KSFunctionDeclaration?
        get() = TODO("Not yet implemented")
    override val qualifiedName: KSName?
        get() = fqName
    override val simpleName: KSName
        get() = KSNameImpl(fqName.getShortName())
    override val superTypes: Sequence<KSTypeReference>
        get() = TODO("Not yet implemented")
    override val typeParameters: List<KSTypeParameter>
        get() = TODO("Not yet implemented")

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        TODO("Not yet implemented")
    }

    override fun asStarProjectedType(): KSType {
        TODO("Not yet implemented")
    }

    override fun asType(typeArguments: List<KSTypeArgument>): KSType {
        TODO("Not yet implemented")
    }

    override fun findActuals(): Sequence<KSDeclaration> {
        TODO("Not yet implemented")
    }

    override fun findExpects(): Sequence<KSDeclaration> {
        TODO("Not yet implemented")
    }

    override fun getAllFunctions(): Sequence<KSFunctionDeclaration> {
        TODO("Not yet implemented")
    }

    override fun getAllProperties(): Sequence<KSPropertyDeclaration> {
        TODO("Not yet implemented")
    }

    override fun getSealedSubclasses(): Sequence<KSClassDeclaration> {
        TODO("Not yet implemented")
    }
}
