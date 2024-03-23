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

import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.processing.impl.ResolverImpl
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.ksp.symbol.impl.binary.KSTypeReferenceDescriptorImpl
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.psi.KtPropertyAccessor

class KSPropertyGetterImpl private constructor(ktPropertyGetter: KtPropertyAccessor) :
    KSPropertyAccessorImpl(ktPropertyGetter),
    io.github.bkmbigo.gallery.ksp.symbol.KSPropertyGetter {
    companion object : KSObjectCache<KtPropertyAccessor, KSPropertyGetterImpl>() {
        fun getCached(ktPropertyGetter: KtPropertyAccessor) = cache.getOrPut(ktPropertyGetter) {
            KSPropertyGetterImpl(ktPropertyGetter)
        }
    }

    override val returnType: KSTypeReference? by lazy {
        val property = ktPropertyGetter.property
        if (property.typeReference != null) {
            KSTypeReferenceImpl.getCached(property.typeReference!!)
        } else {
            val desc = ResolverImpl.instance!!.resolveDeclaration(property) as PropertyDescriptor
            KSTypeReferenceDescriptorImpl.getCached(desc.returnType!!, origin, this)
        }
    }

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitPropertyGetter(this, data)
    }

    override fun toString(): String {
        return "$receiver.getter()"
    }
}
