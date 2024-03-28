/*
 * Copyright 2022 Google LLC
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
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

package io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.resolved

import io.github.bkmbigo.gallery.ksp.common.IdKeyPair
import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.Deferrable
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.Restorable
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.annotations
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotation
import io.github.bkmbigo.gallery.ksp.symbol.KSNode
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeArgument
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeReference
import io.github.bkmbigo.gallery.ksp.symbol.Origin
import io.github.bkmbigo.gallery.ksp.symbol.Variance
import org.jetbrains.kotlin.analysis.api.KtStarTypeProjection
import org.jetbrains.kotlin.analysis.api.KtTypeArgumentWithVariance
import org.jetbrains.kotlin.analysis.api.KtTypeProjection

class KSTypeArgumentResolvedImpl private constructor(
    private val ktTypeProjection: KtTypeProjection,
    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?
) : KSTypeArgument, Deferrable {
    companion object : KSObjectCache<IdKeyPair<KtTypeProjection, KSNode?>, KSTypeArgumentResolvedImpl>() {
        fun getCached(ktTypeProjection: KtTypeProjection, parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode? = null) =
            cache.getOrPut(IdKeyPair(ktTypeProjection, parent)) { KSTypeArgumentResolvedImpl(ktTypeProjection, parent) }
    }

    override val variance: Variance by lazy {
        when (ktTypeProjection) {
            is KtStarTypeProjection -> Variance.STAR
            is KtTypeArgumentWithVariance -> {
                when (ktTypeProjection.variance) {
                    org.jetbrains.kotlin.types.Variance.INVARIANT -> Variance.INVARIANT
                    org.jetbrains.kotlin.types.Variance.IN_VARIANCE -> Variance.CONTRAVARIANT
                    org.jetbrains.kotlin.types.Variance.OUT_VARIANCE -> Variance.COVARIANT
                    else -> throw IllegalStateException("Unexpected variance")
                }
            }
        }
    }

    override val type: KSTypeReference? by lazy {
        ktTypeProjection.type?.let { KSTypeReferenceResolvedImpl.getCached(it, this@KSTypeArgumentResolvedImpl) }
    }

    override val annotations: Sequence<KSAnnotation> by lazy {
        ktTypeProjection.type?.annotations(this) ?: emptySequence()
    }

    override val origin: Origin = parent?.origin ?: Origin.SYNTHETIC

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location
        get() = TODO("Not yet implemented")

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeArgument(this, data)
    }

    override fun toString(): String {
        return "$variance $type"
    }

    override fun defer(): Restorable? {
        TODO("Not yet implemented")
    }
}
