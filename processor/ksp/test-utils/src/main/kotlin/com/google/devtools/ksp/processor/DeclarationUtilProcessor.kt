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

import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSTopDownVisitor

class DeclarationUtilProcessor : AbstractTestProcessor() {
    private val result = mutableListOf<String>()

    override fun toResult(): List<String> {
        return result
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val visitor = DeclarationCollector()
        resolver.getNewFiles().forEach { it.accept(visitor, result) }
        return emptyList()
    }
}

class DeclarationCollector : KSTopDownVisitor<MutableCollection<String>, Unit>() {
    override fun defaultHandler(node: KSNode, data: MutableCollection<String>) {
    }

    private fun KSDeclaration.toSignature(): String {
        qualifiedName?.let {
            return it.asString()
        }
        val parentSignature = parentDeclaration?.toSignature() ?: ""
        return "$parentSignature / ${simpleName.asString()}"
    }

    override fun visitDeclaration(declaration: KSDeclaration, data: MutableCollection<String>) {
        data.add(
            "${declaration.toSignature()}: " + listOf(
                "internal" to declaration.isInternal(),
                "local" to declaration.isLocal(),
                "private" to declaration.isPrivate(),
                "protected" to declaration.isProtected(),
                "public" to declaration.isPublic(),
                "open" to declaration.isOpen()
            ).filter { it.second }.joinToString(" ") {
                it.first
            }
        )
    }
}
