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

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.visitor.KSTopDownVisitor

class JavaModifierProcessor : AbstractTestProcessor() {
    val results = mutableListOf<String>()

    override fun toResult(): List<String> {
        return results.sorted()
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation("Test")
            .map {
                it as KSClassDeclaration
            }
            .forEach {
                it.superTypes.single().resolve().declaration.accept(ModifierVisitor(resolver), Unit)
            }
        return emptyList()
    }

    inner class ModifierVisitor(val resolver: Resolver) : KSTopDownVisitor<Unit, Unit>() {
        override fun defaultHandler(node: KSNode, data: Unit) {
        }

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            results.add(classDeclaration.toSignature())
            classDeclaration.declarations.forEach { it.accept(this, data) }
        }

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            results.add(property.toSignature())
        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            results.add(function.toSignature())
        }

        @OptIn(KspExperimental::class)
        private fun KSDeclaration.toSignature(): String {
            val parent = parentDeclaration
            val id = qualifiedName?.asString()
                ?: "${parentDeclaration?.qualifiedName?.asString()}.${simpleName.asString()}"
            val modifiersSignature = modifiers.map { it.toString() }.sorted().joinToString(" ")
            val extras = resolver.effectiveJavaModifiers(this).map { it.toString() }.sorted().joinToString(" ").trim()
            return "$id: $modifiersSignature".trim() + " : " + extras
        }
    }
}
