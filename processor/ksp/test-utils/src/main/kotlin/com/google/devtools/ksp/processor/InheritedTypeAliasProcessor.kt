/*
 * Copyright 2022 Google LLC
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
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

package com.google.devtools.ksp.processor

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated

class InheritedTypeAliasProcessor : AbstractTestProcessor() {
    val results = mutableListOf<String>()

    override fun toResult(): List<String> {
        return results
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val sub = resolver.getClassDeclarationByName("Sub")!!
        val sup = resolver.getClassDeclarationByName("Super")!!
        sub.getAllFunctions().single { it.simpleName.asString() == "foo" }.let { func ->
            func.parameters.forEach {
                it.type.element?.typeArguments?.joinToString(prefix = "sub: ${it.name?.asString()} :") {
                    it.toString()
                }?.let { results.add(it) }
            }
        }
        sub.getAllProperties().forEach { prop ->
            prop.type.element?.typeArguments?.joinToString(prefix = "sub: ${prop.simpleName.asString()} :") {
                it.toString()
            }?.let { results.add(it) }
        }
        sup.getAllFunctions().single { it.simpleName.asString() == "foo" }.let { func ->
            func.parameters.forEach {
                it.type.element?.typeArguments?.joinToString(prefix = "super: ${it.name?.asString()} :") {
                    it.toString()
                }?.let { results.add(it) }
            }
        }
        sup.getAllProperties().forEach { prop ->
            prop.type.element?.typeArguments?.joinToString(prefix = "super: ${prop.simpleName.asString()} :") {
                it.toString()
            }?.let { results.add(it) }
        }
        return emptyList()
    }
}
