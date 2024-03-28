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

import io.github.bkmbigo.gallery.ksp.common.impl.KSNameImpl
import io.github.bkmbigo.gallery.ksp.common.toKSModifiers
import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.processing.impl.ResolverImpl
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.binary.getAllFunctions
import io.github.bkmbigo.gallery.ksp.symbol.impl.binary.getAllProperties
import io.github.bkmbigo.gallery.ksp.symbol.impl.kotlin.KSErrorType
import io.github.bkmbigo.gallery.ksp.symbol.impl.kotlin.KSExpectActualNoImpl
import io.github.bkmbigo.gallery.ksp.symbol.impl.kotlin.getKSTypeCached
import com.intellij.psi.PsiEnumConstant
import com.intellij.psi.PsiJavaFile
import org.jetbrains.kotlin.descriptors.ClassDescriptor

class KSClassDeclarationJavaEnumEntryImpl private constructor(val psi: PsiEnumConstant) :
    io.github.bkmbigo.gallery.ksp.symbol.KSClassDeclaration,
    KSDeclarationJavaImpl(psi),
    KSExpectActual by KSExpectActualNoImpl() {
    companion object : KSObjectCache<PsiEnumConstant, KSClassDeclarationJavaEnumEntryImpl>() {
        fun getCached(psi: PsiEnumConstant) = cache.getOrPut(psi) { KSClassDeclarationJavaEnumEntryImpl(psi) }
    }

    override val origin = Origin.JAVA

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        psi.toLocation()
    }

    override val annotations: Sequence<KSAnnotation> by lazy {
        psi.annotations.asSequence().map { KSAnnotationJavaImpl.getCached(it) }
    }

    override val classKind: ClassKind = ClassKind.ENUM_ENTRY

    override val containingFile: KSFile? by lazy {
        KSFileJavaImpl.getCached(psi.containingFile as PsiJavaFile)
    }

    override val isCompanionObject = false

    override fun getSealedSubclasses(): Sequence<io.github.bkmbigo.gallery.ksp.symbol.KSClassDeclaration> = emptySequence()

    private val descriptor: ClassDescriptor? by lazy {
        ResolverImpl.instance!!.resolveJavaDeclaration(psi) as ClassDescriptor
    }

    override fun getAllFunctions(): Sequence<KSFunctionDeclaration> =
        descriptor?.getAllFunctions() ?: emptySequence()

    override fun getAllProperties(): Sequence<KSPropertyDeclaration> =
        descriptor?.getAllProperties() ?: emptySequence()

    override val declarations: Sequence<KSDeclaration> = emptySequence()

    override val modifiers: Set<Modifier> by lazy {
        psi.toKSModifiers()
    }

    override val parentDeclaration: KSDeclaration? by lazy {
        psi.findParentDeclaration()
    }

    override val primaryConstructor: KSFunctionDeclaration? = null

    override val qualifiedName: KSName by lazy {
        KSNameImpl.getCached("${parentDeclaration!!.qualifiedName!!.asString()}.${psi.name}")
    }

    override val simpleName: KSName by lazy {
        KSNameImpl.getCached(psi.name)
    }

    override val superTypes: Sequence<KSTypeReference> = emptySequence()

    override val typeParameters: List<KSTypeParameter> = emptyList()

    // Enum can't have type parameters.
    override fun asType(typeArguments: List<KSTypeArgument>): KSType {
        if (typeArguments.isNotEmpty())
            return KSErrorType
        return asStarProjectedType()
    }

    override fun asStarProjectedType(): KSType {
        return getKSTypeCached(descriptor!!.defaultType)
    }

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitClassDeclaration(this, data)
    }
}
