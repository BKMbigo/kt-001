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

package com.google.devtools.ksp.processor

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class DeclarationInconsistencyProcessor : AbstractTestProcessor() {
    val results = mutableListOf<String>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val numberClass = resolver.getClassDeclarationByName("kotlin.Number")!!
        val serializable = numberClass.superTypes.first {
            it.resolve().declaration.qualifiedName?.asString() == "java.io.Serializable"
        }.resolve().declaration as KSClassDeclaration
        val serizableDirect = resolver.getClassDeclarationByName("java.io.Serializable")!!
        results.add("via type: ${serializable.qualifiedName?.asString()}")
        serializable.getAllFunctions().forEach {
            results.add(it.simpleName.asString())
        }
        results.add("via find declaration: ${serizableDirect.qualifiedName?.asString()}")
        serizableDirect.getAllFunctions().forEach {
            results.add(it.simpleName.asString())
        }
        return emptyList()
    }

    override fun toResult(): List<String> {
        return results
    }
}
