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

import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.ksp.symbol.KSType
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeParameter

/**
 * Used when `ResolverImpl.asMemberOf` is called with an error type or function declaration cannot be found.
 */
class KSFunctionErrorImpl(
    private val declaration: KSFunctionDeclaration
) : io.github.bkmbigo.gallery.ksp.symbol.KSFunction {
    override val isError: Boolean = true

    override val returnType: KSType = KSErrorType

    override val parameterTypes: List<KSType?>
        get() = declaration.parameters.map {
            KSErrorType
        }
    override val typeParameters: List<KSTypeParameter>
        get() = emptyList()

    override val extensionReceiverType: KSType?
        get() = declaration.extensionReceiver?.let {
            KSErrorType
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KSFunctionErrorImpl

        if (declaration != other.declaration) return false

        return true
    }

    override fun hashCode(): Int {
        return declaration.hashCode()
    }
}
