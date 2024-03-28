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

package io.github.bkmbigo.gallery.ksp.symbol.impl.binary

import io.github.bkmbigo.gallery.ksp.common.impl.KSNameImpl
import io.github.bkmbigo.gallery.ksp.processing.impl.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.*
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.resolve.calls.components.hasDefaultValue
import org.jetbrains.kotlin.resolve.calls.components.isVararg

class KSValueParameterDescriptorImpl private constructor(
    val descriptor: ValueParameterDescriptor,
    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?
) : KSValueParameter {
    companion object : KSObjectCache<Pair<ValueParameterDescriptor, io.github.bkmbigo.gallery.ksp.symbol.KSNode?>, KSValueParameterDescriptorImpl>() {
        fun getCached(descriptor: ValueParameterDescriptor, parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?) = cache
            .getOrPut(Pair(descriptor, parent)) { KSValueParameterDescriptorImpl(descriptor, parent) }
    }

    override val origin by lazy {
        descriptor.origin
    }

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location =
        io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation

    override val annotations: Sequence<KSAnnotation> by lazy {
        descriptor.annotations.asSequence().map { KSAnnotationDescriptorImpl.getCached(it, this) }
    }

    override val isCrossInline: Boolean = descriptor.isCrossinline

    override val isNoInline: Boolean = descriptor.isNoinline

    override val isVararg: Boolean = descriptor.isVararg

    override val isVal: Boolean = !descriptor.isVar

    override val isVar: Boolean = descriptor.isVar

    override val name: KSName? by lazy {
        KSNameImpl.getCached(descriptor.name.asString())
    }

    override val type: KSTypeReference by lazy {
        // Descriptor wraps vararg with Array<>, to align with the actual behavior in source.
        if (isVararg) {
            KSTypeReferenceDescriptorImpl.getCached(descriptor.varargElementType!!, origin, this)
        } else {
            KSTypeReferenceDescriptorImpl.getCached(descriptor.type, origin, this)
        }
    }

    override val hasDefault: Boolean = descriptor.hasDefaultValue()

    override val defaultExpression: KSExpression?
        get() = throw IllegalStateException("Not implemented in KSP1 binary")

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitValueParameter(this, data)
    }

    override fun toString(): String {
        return name?.asString() ?: "_"
    }
}
