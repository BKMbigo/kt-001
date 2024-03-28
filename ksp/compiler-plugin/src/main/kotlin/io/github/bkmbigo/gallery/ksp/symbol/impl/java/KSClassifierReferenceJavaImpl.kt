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

import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.getInstanceForCurrentRound
import io.github.bkmbigo.gallery.ksp.symbol.impl.toLocation
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.psi.impl.source.PsiClassReferenceType

class KSClassifierReferenceJavaImpl private constructor(
    val psi: PsiClassType,
    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode
) : KSClassifierReference {
    companion object : KSObjectCache<Pair<PsiClassType, io.github.bkmbigo.gallery.ksp.symbol.KSNode>, KSClassifierReferenceJavaImpl>() {
        fun getCached(psi: PsiClassType, parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode): KSClassifierReferenceJavaImpl {
            val curParent = getInstanceForCurrentRound(parent) as KSTypeReference
            return cache.getOrPut(Pair(psi, curParent)) { KSClassifierReferenceJavaImpl(psi, curParent) }
        }
    }

    override val origin = Origin.JAVA

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        (psi as? PsiJavaCodeReferenceElement)?.toLocation() ?: io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation
    }

    override val qualifier: KSClassifierReference? by lazy {
        val qualifierReference = (psi as? PsiClassReferenceType)?.reference?.qualifier as? PsiJavaCodeReferenceElement
            ?: return@lazy null
        val qualifierType = PsiClassReferenceType(qualifierReference, psi.languageLevel)
        getCached(qualifierType, parent)
    }

    // PsiClassType.parameters is semantically argument
    override val typeArguments: List<KSTypeArgument> by lazy {
        psi.parameters.map { KSTypeArgumentJavaImpl.getCached(it, this) }
    }

    override fun referencedName(): String {
        return psi.className + if (psi.parameterCount > 0) "<${
        psi.parameters.joinToString(", ") {
            it.presentableText
        }
        }>" else ""
    }

    override fun toString() = referencedName()
}
