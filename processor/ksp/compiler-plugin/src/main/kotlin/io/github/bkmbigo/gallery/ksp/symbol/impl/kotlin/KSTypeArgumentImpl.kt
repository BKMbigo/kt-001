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
import io.github.bkmbigo.gallery.ksp.common.memoized
import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotation
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeArgument
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeReference
import io.github.bkmbigo.gallery.ksp.symbol.Origin
import io.github.bkmbigo.gallery.ksp.symbol.Variance
import io.github.bkmbigo.gallery.ksp.symbol.impl.toLocation
import org.jetbrains.kotlin.psi.KtProjectionKind
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.KtUserType

abstract class KSTypeArgumentImpl : KSTypeArgument {
    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeArgument(this, data)
    }

    override fun toString(): String {
        return "$variance $type"
    }
}

class KSTypeArgumentKtImpl private constructor(val ktTypeArgument: KtTypeProjection) : KSTypeArgumentImpl() {
    companion object : KSObjectCache<KtTypeProjection, KSTypeArgumentKtImpl>() {
        fun getCached(ktTypeArgument: KtTypeProjection) = cache.getOrPut(ktTypeArgument) {
            KSTypeArgumentKtImpl(ktTypeArgument)
        }
    }

    override val origin = Origin.KOTLIN

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        ktTypeArgument.toLocation()
    }

    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode? by lazy {
        ktTypeArgument.findParentOfType<KtUserType>()?.let { KSClassifierReferenceImpl.getCached(it) }
    }

    override val variance: Variance by lazy {
        when (ktTypeArgument.projectionKind) {
            KtProjectionKind.STAR -> Variance.STAR
            KtProjectionKind.IN -> Variance.CONTRAVARIANT
            KtProjectionKind.NONE -> Variance.INVARIANT
            KtProjectionKind.OUT -> Variance.COVARIANT
        }
    }

    override val type: KSTypeReference? by lazy {
        if (ktTypeArgument.typeReference != null) {
            KSTypeReferenceImpl.getCached(ktTypeArgument.typeReference!!)
        } else {
            null
        }
    }

    override val annotations: Sequence<KSAnnotation> by lazy {
        ktTypeArgument.annotationEntries.asSequence().map { KSAnnotationImpl.getCached(it) }.memoized()
    }
}
