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

import io.github.bkmbigo.gallery.ksp.processing.impl.findActualsInKSDeclaration
import io.github.bkmbigo.gallery.ksp.processing.impl.findExpectsInKSDeclaration
import io.github.bkmbigo.gallery.ksp.symbol.KSDeclaration
import io.github.bkmbigo.gallery.ksp.symbol.KSExpectActual
import org.jetbrains.kotlin.descriptors.MemberDescriptor

class KSExpectActualDescriptorImpl(val descriptor: MemberDescriptor) : KSExpectActual {
    override val isExpect: Boolean = descriptor.isExpect

    override val isActual: Boolean = descriptor.isActual

    private val expects: Sequence<KSDeclaration> by lazy {
        descriptor.findExpectsInKSDeclaration()
    }

    override fun findExpects(): Sequence<KSDeclaration> {
        if (!isActual)
            return emptySequence()
        return expects
    }

    private val actuals: Sequence<KSDeclaration> by lazy {
        descriptor.findActualsInKSDeclaration()
    }

    override fun findActuals(): Sequence<KSDeclaration> {
        if (!isExpect)
            return emptySequence()
        return actuals
    }
}
