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

import io.github.bkmbigo.gallery.ksp.common.IdKeyTriple
import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.impl.recordLookup
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.Deferrable
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.KSClassDeclarationImpl
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.KSErrorType
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.KSTypeImpl
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.KSTypeParameterImpl
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.Restorable
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.analyze
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.annotations
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.classifierSymbol
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.render
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.toClassifierReference
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.toLocation
import io.github.bkmbigo.gallery.ksp.symbol.*
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiTypeParameter
import com.intellij.psi.impl.source.PsiClassReferenceType
import io.github.bkmbigo.gallery.ksp.symbol.KSNode
import org.jetbrains.kotlin.analysis.api.types.*
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtTypeParameter

class KSTypeReferenceResolvedImpl private constructor(
    private val ktType: KtType,
    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?,
    private val index: Int
) : KSTypeReference, Deferrable {
    companion object : KSObjectCache<IdKeyTriple<KtType, KSNode?, Int>, KSTypeReference>() {
        fun getCached(type: KtType, parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode? = null, index: Int = -1): KSTypeReference =
            cache.getOrPut(IdKeyTriple(type, parent, index)) { KSTypeReferenceResolvedImpl(type, parent, index) }
    }

    override val element: KSReferenceElement? by lazy {
        if (parent == null || parent.origin == Origin.SYNTHETIC) {
            null
        } else {
            ktType.toClassifierReference(this)
        }
    }

    override fun resolve(): KSType {
        analyze { recordLookup(ktType, parent) }
        // TODO: non exist type returns KtNonErrorClassType, check upstream for KtClassErrorType usage.
        return if (
            analyze {
                ktType is KtClassErrorType || (ktType.classifierSymbol() == null)
            }
        ) {
            KSErrorType
        } else {
            KSTypeImpl.getCached(ktType)
        }
    }

    override val annotations: Sequence<KSAnnotation> by lazy {
        ktType.annotations(this)
    }

    override val origin: Origin = parent?.origin ?: Origin.SYNTHETIC

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        if (index != -1) {
            parent?.location ?: io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation
        } else {
            when (parent) {
                is KSClassDeclarationImpl -> {
                    when (val psi = parent.ktClassOrObjectSymbol.psi) {
                        is KtClassOrObject -> psi.superTypeListEntries.get(index).toLocation()
                        is PsiClass -> (psi as? PsiClassReferenceType)?.reference?.toLocation() ?: io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation
                        else -> io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation
                    }
                }
                is KSTypeParameterImpl -> {
                    when (val psi = parent.ktTypeParameterSymbol.psi) {
                        is KtTypeParameter -> parent.location
                        is PsiTypeParameter -> (psi.extendsListTypes[index] as? PsiClassReferenceType)
                            ?.reference?.toLocation() ?: io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation
                        else -> io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation
                    }
                }
                else -> io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation
            }
        }
    }

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeReference(this, data)
    }

    override val modifiers: Set<Modifier>
        get() = if (ktType is KtFunctionalType && ktType.isSuspend) {
            setOf(Modifier.SUSPEND)
        } else {
            emptySet()
        }

    override fun toString(): String {
        return ktType.render()
    }

    override fun defer(): Restorable? {
        TODO("Not yet implemented")
    }
}
