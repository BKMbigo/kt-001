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

package io.github.bkmbigo.gallery.ksp.symbol.impl.java

import io.github.bkmbigo.gallery.ksp.common.memoized
import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.kotlin.KSTypeArgumentImpl
import io.github.bkmbigo.gallery.ksp.symbol.impl.toLocation
import com.intellij.psi.PsiType
import com.intellij.psi.PsiWildcardType
import com.intellij.psi.impl.source.PsiClassReferenceType

class KSTypeArgumentJavaImpl private constructor(
    val psi: PsiType,
    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?
) : KSTypeArgumentImpl() {
    companion object : KSObjectCache<PsiType, KSTypeArgumentJavaImpl>() {
        fun getCached(psi: PsiType, parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?) = cache.getOrPut(psi) { KSTypeArgumentJavaImpl(psi, parent) }
    }

    override val origin = Origin.JAVA

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        (psi as? PsiClassReferenceType)?.reference?.toLocation() ?: io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation
    }

    override val annotations: Sequence<KSAnnotation> by lazy {
        psi.annotations.asSequence().map { KSAnnotationJavaImpl.getCached(it) }.memoized()
    }

    // Could be unbounded, need to model unbdouned type argument.
    override val type: KSTypeReference? by lazy {
        io.github.bkmbigo.gallery.ksp.symbol.impl.java.KSTypeReferenceJavaImpl.getCached(psi, this)
    }

    override val variance: Variance by lazy {
        if (psi is PsiWildcardType) {
            when {
                psi.isExtends -> Variance.COVARIANT
                psi.isSuper -> Variance.CONTRAVARIANT
                psi.bound == null -> Variance.STAR
                else -> Variance.INVARIANT
            }
        } else {
            Variance.INVARIANT
        }
    }
}
