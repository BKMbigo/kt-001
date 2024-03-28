/*
 * Copyright 2020 Google LLC
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.bkmbigo.gallery.ksp.symbol

/**
 * A visitor for program elements
 */
interface KSVisitor<D, R> {
    fun visitNode(node: io.github.bkmbigo.gallery.ksp.symbol.KSNode, data: D): R

    fun visitAnnotated(annotated: io.github.bkmbigo.gallery.ksp.symbol.KSAnnotated, data: D): R

    fun visitAnnotation(annotation: io.github.bkmbigo.gallery.ksp.symbol.KSAnnotation, data: D): R

    fun visitModifierListOwner(modifierListOwner: io.github.bkmbigo.gallery.ksp.symbol.KSModifierListOwner, data: D): R

    fun visitDeclaration(declaration: io.github.bkmbigo.gallery.ksp.symbol.KSDeclaration, data: D): R

    fun visitDeclarationContainer(declarationContainer: io.github.bkmbigo.gallery.ksp.symbol.KSDeclarationContainer, data: D): R

    fun visitDynamicReference(reference: io.github.bkmbigo.gallery.ksp.symbol.KSDynamicReference, data: D): R

    fun visitFile(file: io.github.bkmbigo.gallery.ksp.symbol.KSFile, data: D): R

    fun visitFunctionDeclaration(function: io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration, data: D): R

    fun visitCallableReference(reference: io.github.bkmbigo.gallery.ksp.symbol.KSCallableReference, data: D): R

    fun visitParenthesizedReference(reference: io.github.bkmbigo.gallery.ksp.symbol.KSParenthesizedReference, data: D): R

    fun visitPropertyDeclaration(property: io.github.bkmbigo.gallery.ksp.symbol.KSPropertyDeclaration, data: D): R

    fun visitPropertyAccessor(accessor: io.github.bkmbigo.gallery.ksp.symbol.KSPropertyAccessor, data: D): R

    fun visitPropertyGetter(getter: io.github.bkmbigo.gallery.ksp.symbol.KSPropertyGetter, data: D): R

    fun visitPropertySetter(setter: io.github.bkmbigo.gallery.ksp.symbol.KSPropertySetter, data: D): R

    fun visitReferenceElement(element: io.github.bkmbigo.gallery.ksp.symbol.KSReferenceElement, data: D): R

    fun visitTypeAlias(typeAlias: io.github.bkmbigo.gallery.ksp.symbol.KSTypeAlias, data: D): R

    fun visitTypeArgument(typeArgument: io.github.bkmbigo.gallery.ksp.symbol.KSTypeArgument, data: D): R

    fun visitClassDeclaration(classDeclaration: io.github.bkmbigo.gallery.ksp.symbol.KSClassDeclaration, data: D): R

    fun visitTypeParameter(typeParameter: io.github.bkmbigo.gallery.ksp.symbol.KSTypeParameter, data: D): R

    fun visitTypeReference(typeReference: io.github.bkmbigo.gallery.ksp.symbol.KSTypeReference, data: D): R

    fun visitValueParameter(valueParameter: io.github.bkmbigo.gallery.ksp.symbol.KSValueParameter, data: D): R

    fun visitValueArgument(valueArgument: io.github.bkmbigo.gallery.ksp.symbol.KSValueArgument, data: D): R

    fun visitClassifierReference(reference: io.github.bkmbigo.gallery.ksp.symbol.KSClassifierReference, data: D): R

    fun visitDefNonNullReference(reference: io.github.bkmbigo.gallery.ksp.symbol.KSDefNonNullReference, data: D): R
}
