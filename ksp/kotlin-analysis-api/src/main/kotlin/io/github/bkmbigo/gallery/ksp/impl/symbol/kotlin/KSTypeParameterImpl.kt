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

package io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin

import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.common.impl.KSNameImpl
import io.github.bkmbigo.gallery.ksp.common.impl.KSTypeReferenceSyntheticImpl
import io.github.bkmbigo.gallery.ksp.impl.ResolverAAImpl
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.resolved.KSTypeReferenceResolvedImpl
import io.github.bkmbigo.gallery.ksp.symbol.*
import org.jetbrains.kotlin.analysis.api.symbols.KtTypeParameterSymbol
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeParameterListOwner

class KSTypeParameterImpl private constructor(internal val ktTypeParameterSymbol: KtTypeParameterSymbol) :
    KSTypeParameter,
    AbstractKSDeclarationImpl(ktTypeParameterSymbol),
    KSExpectActual by KSExpectActualImpl(ktTypeParameterSymbol) {
    companion object : KSObjectCache<KtTypeParameterSymbol, KSTypeParameterImpl>() {
        fun getCached(ktTypeParameterSymbol: KtTypeParameterSymbol) =
            cache.getOrPut(ktTypeParameterSymbol) { KSTypeParameterImpl(ktTypeParameterSymbol) }
    }

    override val name: KSName by lazy {
        KSNameImpl.getCached(ktTypeParameterSymbol.name.asString())
    }

    override val variance: Variance by lazy {
        when (ktTypeParameterSymbol.variance) {
            org.jetbrains.kotlin.types.Variance.IN_VARIANCE -> Variance.CONTRAVARIANT
            org.jetbrains.kotlin.types.Variance.OUT_VARIANCE -> Variance.COVARIANT
            org.jetbrains.kotlin.types.Variance.INVARIANT -> Variance.INVARIANT
        }
    }

    override val isReified: Boolean = ktTypeParameterSymbol.isReified

    override val bounds: Sequence<KSTypeReference> by lazy {
        when (val psi = ktTypeParameterSymbol.psi) {
            is KtTypeParameter -> {
                val owner = (parentDeclaration as? AbstractKSDeclarationImpl)
                    ?.ktDeclarationSymbol?.psi as? KtTypeParameterListOwner
                val list = sequenceOf(psi.extendsBound)
                if (owner != null) {
                    list.plus(
                        owner.typeConstraints
                            .filter {
                                it.subjectTypeParameterName!!.getReferencedName() == psi.nameAsSafeName.asString()
                            }
                            .map { it.boundTypeReference }
                    )
                }
                list.filterNotNull().map { KSTypeReferenceImpl.getCached(it, this) }
            }
            else -> {
                ktTypeParameterSymbol.upperBounds.asSequence().mapIndexed { index, type ->
                    KSTypeReferenceResolvedImpl.getCached(type, this@KSTypeParameterImpl, index)
                }
            }
        }.ifEmpty {
            sequenceOf(
                KSTypeReferenceSyntheticImpl.getCached(ResolverAAImpl.instance.builtIns.anyType.makeNullable(), this)
            )
        }
    }

    override val typeParameters: List<KSTypeParameter> = emptyList()

    override val qualifiedName: KSName? by lazy {
        this.parentDeclaration?.qualifiedName?.let {
            KSNameImpl.getCached("${it.asString()}.${simpleName.asString()}")
        }
    }

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeParameter(this, data)
    }

    override fun defer(): Restorable? {
        return ktTypeParameterSymbol.defer(::getCached)
    }
}
