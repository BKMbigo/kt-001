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
import io.github.bkmbigo.gallery.ksp.symbol.*

class KSDynamicReferenceImpl private constructor(override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?) : KSDynamicReference {
    companion object : KSObjectCache<KSTypeReference, KSDynamicReferenceImpl>() {
        fun getCached(parent: KSTypeReference) = cache.getOrPut(parent) { KSDynamicReferenceImpl(parent) }
    }

    override val origin = Origin.KOTLIN

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation
    }

    override val typeArguments: List<KSTypeArgument> = emptyList()

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitDynamicReference(this, data)
    }

    override fun toString(): String {
        return "<dynamic type>"
    }
}
