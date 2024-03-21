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
package com.google.devtools.ksp.impl.symbol.kotlin

import com.google.devtools.ksp.common.KSObjectCache
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSVisitor
import com.google.devtools.ksp.symbol.Location
import com.google.devtools.ksp.symbol.Origin
import com.google.devtools.ksp.symbol.Variance
import org.jetbrains.kotlin.psi.KtProjectionKind
import org.jetbrains.kotlin.psi.KtTypeProjection

class KSTypeArgumentImpl(private val ktTypeArgument: KtTypeProjection, override val parent: KSNode) : KSTypeArgument {
    companion object : KSObjectCache<KtTypeProjection, KSTypeArgument>() {
        fun getCached(ktTypeArgument: KtTypeProjection, parent: KSNode) = cache.getOrPut(ktTypeArgument) {
            KSTypeArgumentImpl(ktTypeArgument, parent)
        }
    }

    override val origin = Origin.KOTLIN

    override val location: Location by lazy {
        ktTypeArgument.toLocation()
    }

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeArgument(this, data)
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
            KSTypeReferenceImpl.getCached(ktTypeArgument.typeReference!!, this)
        } else {
            null
        }
    }
    override val annotations: Sequence<KSAnnotation>
        get() = emptySequence()

    override fun toString(): String {
        return "$variance $type"
    }
}
