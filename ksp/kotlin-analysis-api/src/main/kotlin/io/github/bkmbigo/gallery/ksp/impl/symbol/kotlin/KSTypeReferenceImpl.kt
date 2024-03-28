/*
 * Copyright 2023 Google LLC
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
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
package io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin

import io.github.bkmbigo.gallery.ksp.ExceptionMessage
import io.github.bkmbigo.gallery.ksp.common.IdKeyPair
import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.impl.recordLookup
import io.github.bkmbigo.gallery.ksp.impl.symbol.util.toKSModifiers
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotation
import io.github.bkmbigo.gallery.ksp.symbol.KSNode
import io.github.bkmbigo.gallery.ksp.symbol.KSReferenceElement
import io.github.bkmbigo.gallery.ksp.symbol.KSType
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeReference
import io.github.bkmbigo.gallery.ksp.symbol.Modifier
import io.github.bkmbigo.gallery.ksp.symbol.Origin
import org.jetbrains.kotlin.analysis.api.annotations.annotations
import org.jetbrains.kotlin.analysis.api.types.KtType
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType

class KSTypeReferenceImpl(
    private val ktTypeReference: KtTypeReference,
    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?
) : KSTypeReference {
    companion object : KSObjectCache<IdKeyPair<KtTypeReference, KSNode?>, KSTypeReference>() {
        fun getCached(ktTypeReference: KtTypeReference, parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode? = null): KSTypeReference {
            return cache.getOrPut(IdKeyPair(ktTypeReference, parent)) {
                KSTypeReferenceImpl(ktTypeReference, parent)
            }
        }
    }

    // Remember to recordLookup if the usage is beyond a type reference.
    private val ktType: KtType by lazy {
        analyze { ktTypeReference.getKtType() }
    }
    override val element: KSReferenceElement? by lazy {
        var typeElement = ktTypeReference.typeElement
        while (typeElement is KtNullableType)
            typeElement = typeElement.innerType
        when (typeElement) {
            // is KtFunctionType -> KSCallableReferenceImpl.getCached(typeElement)
            is KtUserType -> KSClassifierReferenceImpl.getCached(typeElement, this)
            // is KtDynamicType -> KSDynamicReferenceImpl.getCached(this)
            // is KtIntersectionType -> KSDefNonNullReferenceImpl.getCached(typeElement)
            else -> throw IllegalStateException("Unexpected type element ${typeElement?.javaClass}, $ExceptionMessage")
        }
    }

    override fun resolve(): KSType {
        analyze { recordLookup(ktType, parent) }
        return KSTypeImpl.getCached(ktType)
    }

    override val annotations: Sequence<KSAnnotation> by lazy {
        ktType.annotations.map { KSAnnotationImpl.getCached(it) }.asSequence()
    }

    override val origin: Origin = parent?.origin ?: Origin.SYNTHETIC

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        ktTypeReference.toLocation()
    }

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeReference(this, data)
    }

    override val modifiers: Set<Modifier> by lazy {
        val innerModifiers = mutableSetOf<Modifier>()
        visitNullableType {
            innerModifiers.addAll(it.modifierList.toKSModifiers())
        }
        innerModifiers + ktTypeReference.toKSModifiers()
    }

    override fun toString(): String {
        return element.toString()
    }

    private fun visitNullableType(visit: (KtNullableType) -> Unit) {
        var typeElement = ktTypeReference.typeElement
        while (typeElement is KtNullableType) {
            visit(typeElement)
            typeElement = typeElement.innerType
        }
    }
}
