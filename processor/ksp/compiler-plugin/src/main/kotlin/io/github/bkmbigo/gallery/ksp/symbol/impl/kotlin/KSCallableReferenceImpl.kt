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
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.toLocation
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtTypeReference

class KSCallableReferenceImpl private constructor(val ktFunctionType: KtFunctionType) : KSCallableReference {
    companion object : KSObjectCache<KtFunctionType, KSCallableReferenceImpl>() {
        fun getCached(ktFunctionType: KtFunctionType) = cache.getOrPut(ktFunctionType) {
            KSCallableReferenceImpl(ktFunctionType)
        }
    }

    override val origin = Origin.KOTLIN

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        ktFunctionType.toLocation()
    }

    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode? by lazy {
        ktFunctionType.findParentOfType<KtTypeReference>()?.let { KSTypeReferenceImpl.getCached(it) }
    }

    override val typeArguments: List<KSTypeArgument> by lazy {
        ktFunctionType.typeArgumentsAsTypes.map { KSTypeArgumentLiteImpl.getCached(it) }
    }

    override val functionParameters: List<KSValueParameter> by lazy {
        ktFunctionType.parameters.map { KSValueParameterImpl.getCached(it) }
    }

    override val receiverType: KSTypeReference? by lazy {
        if (ktFunctionType.receiver != null) {
            KSTypeReferenceImpl.getCached(ktFunctionType.receiverTypeReference!!)
        } else {
            null
        }
    }

    override val returnType: KSTypeReference by lazy {
        KSTypeReferenceImpl.getCached(ktFunctionType.returnTypeReference!!)
    }

    override fun toString(): String {
        return "${receiverType?.let { "$it." } ?: ""}(${functionParameters
            .joinToString(", ") { it.type.toString() }}) -> $returnType"
    }
}
