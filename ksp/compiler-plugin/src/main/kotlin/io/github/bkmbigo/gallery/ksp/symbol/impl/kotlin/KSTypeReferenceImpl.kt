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

package io.github.bkmbigo.gallery.ksp.symbol.impl.kotlin

import io.github.bkmbigo.gallery.ksp.ExceptionMessage
import io.github.bkmbigo.gallery.ksp.common.memoized
import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.processing.impl.ResolverImpl
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.toKSModifiers
import io.github.bkmbigo.gallery.ksp.symbol.impl.toLocation
import org.jetbrains.kotlin.psi.*

class KSTypeReferenceImpl private constructor(val ktTypeReference: KtTypeReference) : KSTypeReference {
    companion object : KSObjectCache<KtTypeReference, KSTypeReferenceImpl>() {
        fun getCached(ktTypeReference: KtTypeReference) = cache.getOrPut(ktTypeReference) {
            KSTypeReferenceImpl(ktTypeReference)
        }
    }

    override val origin = Origin.KOTLIN

    override val location: Location by lazy {
        ktTypeReference.toLocation()
    }
    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode? by lazy {
        var parentPsi = ktTypeReference.parent
        while (
            parentPsi != null && parentPsi !is KtAnnotationEntry && parentPsi !is KtFunctionType &&
            parentPsi !is KtClassOrObject && parentPsi !is KtFunction && parentPsi !is KtUserType &&
            parentPsi !is KtProperty && parentPsi !is KtTypeAlias && parentPsi !is KtTypeProjection &&
            parentPsi !is KtTypeParameter && parentPsi !is KtParameter
        ) {
            parentPsi = parentPsi.parent
        }
        when (parentPsi) {
            is KtAnnotationEntry -> KSAnnotationImpl.getCached(parentPsi)
            is KtFunctionType -> KSCallableReferenceImpl.getCached(parentPsi)
            is KtClassOrObject -> KSClassDeclarationImpl.getCached(parentPsi)
            is KtFunction -> KSFunctionDeclarationImpl.getCached(parentPsi)
            is KtUserType -> KSClassifierReferenceImpl.getCached(parentPsi)
            is KtProperty -> KSPropertyDeclarationImpl.getCached(parentPsi)
            is KtTypeAlias -> KSTypeAliasImpl.getCached(parentPsi)
            is KtTypeProjection -> KSTypeArgumentKtImpl.getCached(parentPsi)
            is KtTypeParameter -> KSTypeParameterImpl.getCached(parentPsi)
            is KtParameter -> KSValueParameterImpl.getCached(parentPsi)
            else -> null
        }
    }

    // Parenthesized type in grammar seems to be implemented as KtNullableType.
    private fun visitNullableType(visit: (KtNullableType) -> Unit) {
        var typeElement = ktTypeReference.typeElement
        while (typeElement is KtNullableType) {
            visit(typeElement)
            typeElement = typeElement.innerType
        }
    }

    // Annotations and modifiers are only allowed in one of the parenthesized type.
    // https://github.com/JetBrains/kotlin/blob/50e12239ef8141a45c4dca2bf0544be6191ecfb6/compiler/frontend/src/org/jetbrains/kotlin/diagnostics/rendering/DefaultErrorMessages.java#L608
    override val annotations: Sequence<KSAnnotation> by lazy {
        fun List<KtAnnotationEntry>.toKSAnnotations(): Sequence<KSAnnotation> =
            asSequence().map {
                KSAnnotationImpl.getCached(it)
            }

        val innerAnnotations = mutableListOf<Sequence<KSAnnotation>>()
        visitNullableType {
            innerAnnotations.add(it.annotationEntries.toKSAnnotations())
        }

        (ktTypeReference.annotationEntries.toKSAnnotations() + innerAnnotations.asSequence().flatten()).memoized()
    }

    override val modifiers: Set<Modifier> by lazy {
        val innerModifiers = mutableSetOf<Modifier>()
        visitNullableType {
            innerModifiers.addAll(it.modifierList.toKSModifiers())
        }
        ktTypeReference.toKSModifiers() + innerModifiers
    }

    override val element: KSReferenceElement by lazy {
        var typeElement = ktTypeReference.typeElement
        while (typeElement is KtNullableType)
            typeElement = typeElement.innerType
        when (typeElement) {
            is KtFunctionType -> KSCallableReferenceImpl.getCached(typeElement)
            is KtUserType -> KSClassifierReferenceImpl.getCached(typeElement)
            is KtDynamicType -> KSDynamicReferenceImpl.getCached(this)
            is KtIntersectionType -> KSDefNonNullReferenceImpl.getCached(typeElement)
            else -> throw IllegalStateException("Unexpected type element ${typeElement?.javaClass}, $ExceptionMessage")
        }
    }

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeReference(this, data)
    }

    override fun resolve(): KSType = ResolverImpl.instance!!.resolveUserType(this)

    override fun toString(): String {
        return element.toString()
    }
}
