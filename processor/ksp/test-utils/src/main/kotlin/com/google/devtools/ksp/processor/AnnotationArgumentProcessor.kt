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
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.KSVisitorVoid

class AnnotationArgumentProcessor : AbstractTestProcessor() {
    val results = mutableListOf<String>()
    val visitor = ArgumentVisitor()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation("Bar", true).forEach {
            it.annotations.forEach { it.arguments.forEach { it.accept(visitor, Unit) } }
        }

        val C = resolver.getClassDeclarationByName("C")
        C?.annotations?.first()?.arguments?.forEach { results.add(it.value.toString()) }
        val ThrowsClass = resolver.getClassDeclarationByName("ThrowsClass")
        ThrowsClass?.declarations?.filter {
            it.simpleName.asString() == "throwsException"
        }?.forEach {
            it.annotations.single().annotationType.resolve().declaration.let {
                results.add(it.toString())
            }
        }
        return emptyList()
    }

    override fun toResult(): List<String> {
        return results
    }

    inner class ArgumentVisitor : KSVisitorVoid() {
        override fun visitValueArgument(valueArgument: KSValueArgument, data: Unit) {
            if (valueArgument.value is KSType) {
                results.add((valueArgument.value as KSType).declaration.toString())
            } else {
                results.add(valueArgument.value.toString())
            }
        }
    }
}
