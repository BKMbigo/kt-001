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

import io.github.bkmbigo.gallery.ksp.common.IdKeyPair
import io.github.bkmbigo.gallery.ksp.common.impl.KSNameImpl
import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.getInstanceForCurrentRound
import io.github.bkmbigo.gallery.ksp.symbol.impl.toLocation
import com.intellij.psi.PsiParameter
import io.github.bkmbigo.gallery.ksp.symbol.KSNode

class KSValueParameterJavaImpl private constructor(val psi: PsiParameter, override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode) :
    KSValueParameter {
    companion object : KSObjectCache<IdKeyPair<PsiParameter, KSNode>, KSValueParameterJavaImpl>() {
        fun getCached(psi: PsiParameter, parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode): KSValueParameterJavaImpl {
            val curParent = getInstanceForCurrentRound(parent) as io.github.bkmbigo.gallery.ksp.symbol.KSNode
            return cache.getOrPut(IdKeyPair(psi, curParent)) { KSValueParameterJavaImpl(psi, curParent) }
        }
    }

    override val origin = Origin.JAVA

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        psi.toLocation()
    }

    override val annotations: Sequence<KSAnnotation> by lazy {
        psi.annotations.asSequence().map { KSAnnotationJavaImpl.getCached(it) }
    }

    override val isCrossInline: Boolean = false

    override val isNoInline: Boolean = false

    override val isVararg: Boolean = psi.isVarArgs

    override val isVal: Boolean = false

    override val isVar: Boolean = false

    override val name: KSName? by lazy {
        if (psi.name != null) {
            KSNameImpl.getCached(psi.name!!)
        } else {
            null
        }
    }

    override val type: KSTypeReference by lazy {
        io.github.bkmbigo.gallery.ksp.symbol.impl.java.KSTypeReferenceJavaImpl.getCached(psi.type, this)
    }

    override val hasDefault: Boolean = false

    override val defaultExpression: KSExpression? = null

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitValueParameter(this, data)
    }

    override fun toString(): String {
        return name?.asString() ?: "_"
    }
}
