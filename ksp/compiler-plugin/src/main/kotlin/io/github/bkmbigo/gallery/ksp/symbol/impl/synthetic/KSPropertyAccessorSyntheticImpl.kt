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

package io.github.bkmbigo.gallery.ksp.symbol.impl.synthetic

import io.github.bkmbigo.gallery.ksp.processing.impl.findAnnotationFromUseSiteTarget
import io.github.bkmbigo.gallery.ksp.symbol.*

abstract class KSPropertyAccessorSyntheticImpl(ksPropertyDeclaration: KSPropertyDeclaration) :
    io.github.bkmbigo.gallery.ksp.symbol.KSPropertyAccessor {
    override val annotations: Sequence<KSAnnotation> by lazy {
        this.findAnnotationFromUseSiteTarget()
    }

    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location by lazy {
        ksPropertyDeclaration.location
    }

    override val modifiers: Set<Modifier> = emptySet()

    override val origin: Origin = Origin.SYNTHETIC

    override val receiver: KSPropertyDeclaration = ksPropertyDeclaration

    override fun <D, R> accept(visitor: io.github.bkmbigo.gallery.ksp.symbol.KSVisitor<D, R>, data: D): R {
        return visitor.visitPropertyAccessor(this, data)
    }
}
