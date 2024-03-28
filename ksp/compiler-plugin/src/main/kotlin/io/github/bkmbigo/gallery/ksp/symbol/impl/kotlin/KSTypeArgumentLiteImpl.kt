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

import io.github.bkmbigo.gallery.ksp.common.findParentOfType
import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotation
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeReference
import io.github.bkmbigo.gallery.ksp.symbol.Origin
import io.github.bkmbigo.gallery.ksp.symbol.Variance
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtTypeReference

class KSTypeArgumentLiteImpl private constructor(override val type: KSTypeReference, override val variance: Variance) :
    KSTypeArgumentImpl() {
    companion object : KSObjectCache<Pair<KSTypeReference, Variance>, KSTypeArgumentLiteImpl>() {
        fun getCached(type: KSTypeReference, variance: Variance) = cache.getOrPut(Pair(type, variance)) {
            KSTypeArgumentLiteImpl(type, variance)
        }

        fun getCached(type: KtTypeReference) = cache.getOrPut(
            Pair(KSTypeReferenceImpl.getCached(type), Variance.INVARIANT)
        ) {
            KSTypeArgumentLiteImpl(KSTypeReferenceImpl.getCached(type), Variance.INVARIANT)
        }
    }

    override val origin = Origin.KOTLIN

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location =
        io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation

    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode? by lazy {
        (type as? KSTypeReferenceImpl)?.ktTypeReference
            ?.findParentOfType<KtFunctionType>()?.let { KSCallableReferenceImpl.getCached(it) }
    }

    override val annotations: Sequence<KSAnnotation> = type.annotations
}
