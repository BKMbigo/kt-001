package io.github.bkmbigo.gallery.ksp.visitor

import io.github.bkmbigo.gallery.ksp.symbol.*

open class KSValidateVisitor(
    private val predicate: (io.github.bkmbigo.gallery.ksp.symbol.KSNode?, io.github.bkmbigo.gallery.ksp.symbol.KSNode) -> Boolean
) : KSDefaultVisitor<io.github.bkmbigo.gallery.ksp.symbol.KSNode?, Boolean>() {
    private fun validateType(type: KSType): Boolean {
        return !type.isError && !type.arguments.any { it.type?.accept(this, null) == false }
    }

    override fun defaultHandler(node: io.github.bkmbigo.gallery.ksp.symbol.KSNode, data: io.github.bkmbigo.gallery.ksp.symbol.KSNode?): Boolean {
        return true
    }

    override fun visitDeclaration(declaration: KSDeclaration, data: io.github.bkmbigo.gallery.ksp.symbol.KSNode?): Boolean {
        if (!predicate(data, declaration)) {
            return true
        }
        if (declaration.typeParameters.any { !it.accept(this, declaration) }) {
            return false
        }
        return this.visitAnnotated(declaration, data)
    }

    override fun visitDeclarationContainer(declarationContainer: KSDeclarationContainer, data: io.github.bkmbigo.gallery.ksp.symbol.KSNode?): Boolean {
        return declarationContainer.declarations.all {
            !predicate(declarationContainer, it) || it.accept(
                this,
                declarationContainer
            )
        }
    }

    override fun visitTypeParameter(typeParameter: KSTypeParameter, data: io.github.bkmbigo.gallery.ksp.symbol.KSNode?): Boolean {
        return !predicate(data, typeParameter) || typeParameter.bounds.all { it.accept(this, typeParameter) }
    }

    override fun visitAnnotated(annotated: KSAnnotated, data: io.github.bkmbigo.gallery.ksp.symbol.KSNode?): Boolean {
        return !predicate(data, annotated) || annotated.annotations.all { it.accept(this, annotated) }
    }

    override fun visitAnnotation(annotation: KSAnnotation, data: io.github.bkmbigo.gallery.ksp.symbol.KSNode?): Boolean {
        if (!predicate(data, annotation)) {
            return true
        }
        if (!annotation.annotationType.accept(this, annotation)) {
            return false
        }
        if (annotation.arguments.any { !it.accept(this, it) }) {
            return false
        }
        return true
    }

    override fun visitTypeReference(typeReference: KSTypeReference, data: io.github.bkmbigo.gallery.ksp.symbol.KSNode?): Boolean {
        return validateType(typeReference.resolve())
    }

    override fun visitClassDeclaration(classDeclaration: io.github.bkmbigo.gallery.ksp.symbol.KSClassDeclaration, data: io.github.bkmbigo.gallery.ksp.symbol.KSNode?): Boolean {
        if (classDeclaration.asStarProjectedType().isError) {
            return false
        }
        if (!classDeclaration.superTypes.all { it.accept(this, classDeclaration) }) {
            return false
        }
        if (!this.visitDeclaration(classDeclaration, data)) {
            return false
        }
        if (!this.visitDeclarationContainer(classDeclaration, data)) {
            return false
        }
        return true
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: io.github.bkmbigo.gallery.ksp.symbol.KSNode?): Boolean {
        if (function.returnType != null &&
            predicate(function, function.returnType!!) && !function.returnType!!.accept(this, data)
        ) {
            return false
        }
        if (!function.parameters.all { it.accept(this, function) }) {
            return false
        }
        if (!this.visitDeclaration(function, data)) {
            return false
        }
        return true
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: io.github.bkmbigo.gallery.ksp.symbol.KSNode?): Boolean {
        if (predicate(property, property.type) && !property.type.accept(this, data)) {
            return false
        }
        if (!this.visitDeclaration(property, data)) {
            return false
        }
        return true
    }

    override fun visitValueArgument(valueArgument: KSValueArgument, data: io.github.bkmbigo.gallery.ksp.symbol.KSNode?): Boolean {
        fun visitValue(value: Any?): Boolean = when (value) {
            is KSType -> this.validateType(value)
            is KSAnnotation -> this.visitAnnotation(value, data)
            is List<*> -> value.all { visitValue(it) }
            else -> true
        }
        return visitValue(valueArgument.value)
    }

    override fun visitValueParameter(valueParameter: KSValueParameter, data: io.github.bkmbigo.gallery.ksp.symbol.KSNode?): Boolean {
        return valueParameter.type.accept(this, valueParameter)
    }
}
