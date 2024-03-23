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

import io.github.bkmbigo.gallery.ksp.common.impl.KSNameImpl
import io.github.bkmbigo.gallery.ksp.common.memoized
import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.getKSDeclarations
import io.github.bkmbigo.gallery.ksp.symbol.impl.toLocation
import org.jetbrains.kotlin.psi.KtFile

class KSFileImpl private constructor(val file: KtFile) : KSFile {
    companion object : KSObjectCache<KtFile, KSFileImpl>() {
        fun getCached(file: KtFile) = cache.getOrPut(file) { KSFileImpl(file) }
    }

    override val origin = Origin.KOTLIN

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        file.toLocation()
    }

    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode? = null

    override val packageName: KSName by lazy {
        KSNameImpl.getCached(file.packageFqName.asString())
    }

    override val annotations: Sequence<KSAnnotation> by lazy {
        file.annotationEntries.asSequence().map { KSAnnotationImpl.getCached(it) }.memoized()
    }

    override val declarations: Sequence<KSDeclaration> by lazy {
        file.declarations.asSequence().getKSDeclarations().memoized()
    }

    override val fileName: String by lazy {
        file.name
    }

    override val filePath: String by lazy {
        file.virtualFilePath
    }

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitFile(this, data)
    }

    override fun toString(): String {
        return "File: ${this.fileName}"
    }
}
