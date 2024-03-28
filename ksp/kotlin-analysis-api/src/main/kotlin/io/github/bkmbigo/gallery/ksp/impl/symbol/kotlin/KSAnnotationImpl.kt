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

import io.github.bkmbigo.gallery.ksp.common.IdKeyPair
import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.common.impl.KSNameImpl
import io.github.bkmbigo.gallery.ksp.impl.symbol.java.KSValueArgumentLiteImpl
import io.github.bkmbigo.gallery.ksp.impl.symbol.java.calcValue
import io.github.bkmbigo.gallery.ksp.impl.symbol.kotlin.resolved.KSTypeReferenceResolvedImpl
import io.github.bkmbigo.gallery.ksp.symbol.*
import com.intellij.psi.PsiAnnotationMethod
import com.intellij.psi.PsiClass
import io.github.bkmbigo.gallery.ksp.symbol.KSNode
import org.jetbrains.kotlin.analysis.api.annotations.KtAnnotationApplicationWithArgumentsInfo
import org.jetbrains.kotlin.analysis.api.annotations.KtNamedAnnotationValue
import org.jetbrains.kotlin.analysis.api.components.buildClassType
import org.jetbrains.kotlin.analysis.api.symbols.KtSymbolOrigin
import org.jetbrains.kotlin.descriptors.annotations.AnnotationUseSiteTarget.*

// TODO: implement a psi based version of annotation application.
class KSAnnotationImpl private constructor(
    private val annotationApplication: KtAnnotationApplicationWithArgumentsInfo,
    override val parent: KSNode?
) : KSAnnotation {
    companion object : KSObjectCache<IdKeyPair<KtAnnotationApplicationWithArgumentsInfo, KSNode?>, KSAnnotationImpl>() {
        fun getCached(annotationApplication: KtAnnotationApplicationWithArgumentsInfo, parent: KSNode? = null) =
            cache.getOrPut(IdKeyPair(annotationApplication, parent)) { KSAnnotationImpl(annotationApplication, parent) }
    }

    override val annotationType: KSTypeReference by lazy {
        analyze {
            KSTypeReferenceResolvedImpl.getCached(buildClassType(annotationApplication.classId!!))
        }
    }

    override val arguments: List<KSValueArgument> by lazy {
        val presentArgs = annotationApplication.arguments.map { KSValueArgumentImpl.getCached(it, Origin.KOTLIN) }
        val presentNames = presentArgs.mapNotNull { it.name?.asString() }
        val absentArgs = defaultArguments.filter {
            it.name?.asString() !in presentNames
        }
        presentArgs + absentArgs
    }

    override val defaultArguments: List<KSValueArgument> by lazy {
        analyze {
            annotationApplication.classId?.toKtClassSymbol()?.let { symbol ->
                if (symbol.origin == KtSymbolOrigin.JAVA && symbol.psi != null) {
                    (symbol.psi as PsiClass).allMethods.filterIsInstance<PsiAnnotationMethod>()
                        .mapNotNull { annoMethod ->
                            annoMethod.defaultValue?.let {
                                KSValueArgumentLiteImpl.getCached(
                                    KSNameImpl.getCached(annoMethod.name),
                                    calcValue(it),
                                    Origin.SYNTHETIC
                                )
                            }
                        }
                } else {
                    symbol.getMemberScope().getConstructors().singleOrNull()?.let {
                        it.valueParameters.mapNotNull { valueParameterSymbol ->
                            valueParameterSymbol.getDefaultValue()?.let { constantValue ->
                                KSValueArgumentImpl.getCached(
                                    KtNamedAnnotationValue(
                                        valueParameterSymbol.name, constantValue,
                                    ),
                                    Origin.SYNTHETIC
                                )
                            }
                        }
                    }
                }
            } ?: emptyList()
        }
    }

    override val shortName: KSName by lazy {
        KSNameImpl.getCached(annotationApplication.classId!!.shortClassName.asString())
    }

    override val useSiteTarget: AnnotationUseSiteTarget? by lazy {
        when (annotationApplication.useSiteTarget) {
            null -> null
            FILE -> AnnotationUseSiteTarget.FILE
            PROPERTY -> AnnotationUseSiteTarget.PROPERTY
            FIELD -> AnnotationUseSiteTarget.FIELD
            PROPERTY_GETTER -> AnnotationUseSiteTarget.GET
            PROPERTY_SETTER -> AnnotationUseSiteTarget.SET
            RECEIVER -> AnnotationUseSiteTarget.RECEIVER
            CONSTRUCTOR_PARAMETER -> AnnotationUseSiteTarget.PARAM
            SETTER_PARAMETER -> AnnotationUseSiteTarget.SETPARAM
            PROPERTY_DELEGATE_FIELD -> AnnotationUseSiteTarget.DELEGATE
        }
    }

    override val origin: Origin = Origin.KOTLIN

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        annotationApplication.psi?.toLocation() ?: io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation
    }

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitAnnotation(this, data)
    }

    override fun toString(): String {
        return "@${shortName.asString()}"
    }
}
