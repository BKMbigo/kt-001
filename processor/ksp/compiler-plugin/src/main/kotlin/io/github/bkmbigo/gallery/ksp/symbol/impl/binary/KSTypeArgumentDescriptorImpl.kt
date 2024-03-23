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

package io.github.bkmbigo.gallery.ksp.symbol.impl.binary

import io.github.bkmbigo.gallery.ksp.common.IdKeyTriple
import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotation
import io.github.bkmbigo.gallery.ksp.symbol.KSNode
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeReference
import io.github.bkmbigo.gallery.ksp.symbol.Origin
import io.github.bkmbigo.gallery.ksp.symbol.Variance
import io.github.bkmbigo.gallery.ksp.symbol.impl.kotlin.KSTypeArgumentImpl
import org.jetbrains.kotlin.types.TypeProjection

class KSTypeArgumentDescriptorImpl private constructor(
    val descriptor: TypeProjection,
    override val origin: Origin,
    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?
) : KSTypeArgumentImpl() {
    companion object : KSObjectCache<IdKeyTriple<TypeProjection, Origin, KSNode?>, KSTypeArgumentDescriptorImpl>() {
        fun getCached(descriptor: TypeProjection, origin: Origin, parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?) = cache
            .getOrPut(IdKeyTriple(descriptor, origin, parent)) {
                KSTypeArgumentDescriptorImpl(descriptor, origin, parent)
            }
    }

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location =
        io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation

    override val type: KSTypeReference by lazy {
        KSTypeReferenceDescriptorImpl.getCached(descriptor.type, origin, if (parent != null) this else null)
    }

    override val variance: Variance by lazy {
        if (descriptor.isStarProjection)
            Variance.STAR
        else {
            when (descriptor.projectionKind) {
                org.jetbrains.kotlin.types.Variance.IN_VARIANCE -> Variance.CONTRAVARIANT
                org.jetbrains.kotlin.types.Variance.OUT_VARIANCE -> Variance.COVARIANT
                else -> Variance.INVARIANT
            }
        }
    }

    override val annotations: Sequence<KSAnnotation> by lazy {
        descriptor.type.annotations.asSequence().map { KSAnnotationDescriptorImpl.getCached(it, this) }
    }
}
