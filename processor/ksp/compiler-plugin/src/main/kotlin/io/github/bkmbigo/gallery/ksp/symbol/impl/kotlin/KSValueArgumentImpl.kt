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
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotation
import io.github.bkmbigo.gallery.ksp.symbol.KSName
import io.github.bkmbigo.gallery.ksp.symbol.KSValueArgument
import io.github.bkmbigo.gallery.ksp.symbol.Origin

class KSValueArgumentLiteImpl private constructor(
    override val name: KSName,
    override val value: Any?,
    override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?,
    override val origin: Origin
) : KSValueArgumentImpl() {
    companion object : KSObjectCache<Triple<KSName, Any?, io.github.bkmbigo.gallery.ksp.symbol.KSNode>, KSValueArgumentLiteImpl>() {

        fun getCached(name: KSName, value: Any?, parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode, origin: Origin = Origin.KOTLIN) = cache
            .getOrPut(Triple(name, value, parent)) {
                KSValueArgumentLiteImpl(name, value, parent, origin)
            }
    }

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location =
        io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation

    override val annotations: Sequence<KSAnnotation> = emptySequence()

    override val isSpread: Boolean = false
}

abstract class KSValueArgumentImpl : KSValueArgument {
    override fun hashCode(): Int {
        return name.hashCode() * 31 + value.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is KSValueArgument)
            return false

        return other.name == this.name && other.value == this.value
    }

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitValueArgument(this, data)
    }

    override fun toString(): String {
        return "${name?.asString() ?: ""}:$value"
    }
}
