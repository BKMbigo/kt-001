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

import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotation
import io.github.bkmbigo.gallery.ksp.symbol.KSDeclaration
import io.github.bkmbigo.gallery.ksp.symbol.KSReferenceElement
import io.github.bkmbigo.gallery.ksp.symbol.KSType
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeReference
import io.github.bkmbigo.gallery.ksp.symbol.Modifier
import io.github.bkmbigo.gallery.ksp.symbol.Origin
import io.github.bkmbigo.gallery.ksp.symbol.impl.getInstanceForCurrentRound

class KSTypeReferenceDeferredImpl private constructor(
    private val resolver: () -> KSType,
    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?
) : KSTypeReference {
    companion object : KSObjectCache<KSDeclaration, KSTypeReferenceDeferredImpl>() {
        fun getCached(parent: KSDeclaration, resolver: () -> KSType): KSTypeReferenceDeferredImpl {
            val currentParent = parent.getInstanceForCurrentRound() as KSDeclaration
            return cache
                .getOrPut(currentParent) { KSTypeReferenceDeferredImpl(resolver, currentParent) }
        }
    }

    override val origin = Origin.KOTLIN

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location =
        io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation

    override val annotations: Sequence<KSAnnotation> = emptySequence()

    override val element: KSReferenceElement? = null

    override val modifiers: Set<Modifier> = emptySet()

    private val resolved: KSType by lazy {
        resolver()
    }

    override fun resolve(): KSType = resolved

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeReference(this, data)
    }

    override fun toString(): String {
        return resolved.toString()
    }
}
