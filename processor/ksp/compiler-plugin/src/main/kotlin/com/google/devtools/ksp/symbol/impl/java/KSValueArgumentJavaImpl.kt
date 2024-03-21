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

import com.google.devtools.ksp.processing.impl.KSObjectCache
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.Location
import com.google.devtools.ksp.symbol.NonExistLocation
import com.google.devtools.ksp.symbol.Origin
import com.google.devtools.ksp.symbol.impl.kotlin.KSValueArgumentImpl

class KSValueArgumentJavaImpl private constructor(
    override val name: KSName?,
    override val value: Any?,
    override val parent: KSNode?
) : KSValueArgumentImpl() {
    companion object : KSObjectCache<Triple<KSName?, Any?, KSNode?>, KSValueArgumentJavaImpl>() {
        fun getCached(name: KSName?, value: Any?, parent: KSNode?) =
            cache.getOrPut(Triple(name, value, parent)) { KSValueArgumentJavaImpl(name, value, parent) }
    }

    override val origin = Origin.JAVA

    override val location: Location = NonExistLocation

    override val isSpread: Boolean = false

    override val annotations: Sequence<KSAnnotation> = emptySequence()

    override fun toString(): String {
        return "${name?.asString() ?: ""}:$value"
    }
}
