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

package com.google.devtools.ksp.symbol.impl.java

import com.google.devtools.ksp.ExceptionMessage
import com.google.devtools.ksp.common.memoized
import com.google.devtools.ksp.processing.impl.KSObjectCache
import com.google.devtools.ksp.processing.impl.ResolverImpl
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSReferenceElement
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSVisitor
import com.google.devtools.ksp.symbol.Location
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.NonExistLocation
import com.google.devtools.ksp.symbol.Origin
import com.google.devtools.ksp.symbol.impl.binary.KSClassDeclarationDescriptorImpl
import com.google.devtools.ksp.symbol.impl.binary.KSClassifierReferenceDescriptorImpl
import com.google.devtools.ksp.symbol.impl.kotlin.KSErrorType
import com.google.devtools.ksp.symbol.impl.kotlin.KSTypeImpl
import com.google.devtools.ksp.symbol.impl.toLocation
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiType
import com.intellij.psi.PsiWildcardType
import com.intellij.psi.impl.source.PsiClassReferenceType
import org.jetbrains.kotlin.descriptors.NotFoundClasses
import org.jetbrains.kotlin.load.java.NOT_NULL_ANNOTATIONS
import org.jetbrains.kotlin.load.java.NULLABLE_ANNOTATIONS
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.typeUtil.makeNullable

class KSTypeReferenceJavaImpl private constructor(val psi: PsiType, override val parent: KSNode?) : KSTypeReference {
    companion object : KSObjectCache<Pair<PsiType, KSNode?>, KSTypeReferenceJavaImpl>() {
        fun getCached(psi: PsiType, parent: KSNode?) = cache
            .getOrPut(Pair(psi, parent)) { KSTypeReferenceJavaImpl(psi, parent) }
    }

    override val origin = Origin.JAVA

    override val location: Location by lazy {
        (psi as? PsiClassReferenceType)?.reference?.toLocation() ?: NonExistLocation
    }

    override val annotations: Sequence<KSAnnotation> by lazy {
        psi.annotations.asSequence().map { KSAnnotationJavaImpl.getCached(it) }.memoized()
    }

    override val modifiers: Set<Modifier> = emptySet()

    override val element: KSReferenceElement by lazy {
        fun PsiPrimitiveType.toKotlinType(): KotlinType {
            return when (this.name) {
                "int" -> ResolverImpl.instance!!.module.builtIns.intType
                "short" -> ResolverImpl.instance!!.module.builtIns.shortType
                "byte" -> ResolverImpl.instance!!.module.builtIns.byteType
                "long" -> ResolverImpl.instance!!.module.builtIns.longType
                "float" -> ResolverImpl.instance!!.module.builtIns.floatType
                "double" -> ResolverImpl.instance!!.module.builtIns.doubleType
                "char" -> ResolverImpl.instance!!.module.builtIns.charType
                "boolean" -> ResolverImpl.instance!!.module.builtIns.booleanType
                "void" -> ResolverImpl.instance!!.module.builtIns.unitType
                else -> throw IllegalStateException("Unexpected primitive type ${this.name}, $ExceptionMessage")
            }
        }

        val type = if (psi is PsiWildcardType) {
            psi.bound
        } else {
            psi
        }
        when (type) {
            is PsiClassType -> KSClassifierReferenceJavaImpl.getCached(type, this)
            is PsiWildcardType -> KSClassifierReferenceJavaImpl.getCached(type.extendsBound as PsiClassType, this)
            is PsiPrimitiveType -> KSClassifierReferenceDescriptorImpl.getCached(type.toKotlinType(), origin, this)
            is PsiArrayType -> {
                val componentType = ResolverImpl.instance!!.resolveJavaType(type.componentType, this)
                if (type.componentType !is PsiPrimitiveType) {
                    KSClassifierReferenceDescriptorImpl.getCached(
                        ResolverImpl.instance!!.module.builtIns.getArrayType(Variance.OUT_VARIANCE, componentType),
                        origin,
                        this
                    )
                } else {
                    KSClassifierReferenceDescriptorImpl.getCached(
                        ResolverImpl.instance!!.module.builtIns
                            .getPrimitiveArrayKotlinTypeByPrimitiveKotlinType(componentType)!!,
                        origin, this
                    )
                }
            }
            null ->
                KSClassifierReferenceDescriptorImpl.getCached(
                    (ResolverImpl.instance!!.builtIns.anyType as KSTypeImpl).kotlinType.makeNullable(), origin, this
                )
            else -> throw IllegalStateException("Unexpected psi type for ${type.javaClass}, $ExceptionMessage")
        }
    }

    override fun resolve(): KSType {
        val resolvedType = ResolverImpl.instance!!.resolveUserType(this)
        val relatedAnnotations = (annotations + ((parent as? KSAnnotated)?.annotations ?: emptySequence()))
            .mapNotNull {
                (it.annotationType.resolve() as? KSTypeImpl)?.kotlinType?.constructor?.declarationDescriptor?.fqNameSafe
            }
        val resolved = if ((resolvedType.declaration as? KSClassDeclarationDescriptorImpl)
            ?.descriptor is NotFoundClasses.MockClassDescriptor
        ) {
            KSErrorType
        } else resolvedType
        val hasNotNull = relatedAnnotations.any { it in NOT_NULL_ANNOTATIONS }
        val hasNullable = relatedAnnotations.any { it in NULLABLE_ANNOTATIONS }
        return if (hasNullable && !hasNotNull) {
            resolved.makeNullable()
        } else if (!hasNullable && hasNotNull) {
            resolved.makeNotNullable()
        } else resolved
    }

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeReference(this, data)
    }

    override fun toString(): String {
        return element.toString()
    }
}
