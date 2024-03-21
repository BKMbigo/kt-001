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
import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

class ImplicitElementProcessor : AbstractTestProcessor() {
    val result: MutableList<String> = mutableListOf()

    override fun toResult(): List<String> {
        return result
    }

    private fun nameAndOrigin(declaration: KSDeclaration) =
        "${declaration.simpleName.asString()}: ${declaration.origin}"

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val ClsClass = resolver.getClassDeclarationByName(resolver.getKSNameFromString("Cls"))!!
        result.add(
            "${
            ClsClass.primaryConstructor?.simpleName?.asString() ?: "<null>"
            }; origin: ${ClsClass.primaryConstructor?.origin}"
        )
        result.add(ClsClass.getConstructors().map { it.toString() }.joinToString(","))
        val ITF = resolver.getClassDeclarationByName(resolver.getKSNameFromString("ITF"))!!
        result.add(ITF.primaryConstructor?.simpleName?.asString() ?: "<null>")
        val JavaClass = resolver.getClassDeclarationByName("JavaClass")!!
        result.add(JavaClass.primaryConstructor?.simpleName?.asString() ?: "<null>")
        result.add(JavaClass.getDeclaredFunctions().map { it.simpleName.asString() }.joinToString(","))
        val readOnly = ClsClass.declarations.single { it.simpleName.asString() == "readOnly" } as KSPropertyDeclaration
        readOnly.getter?.let {
            result.add(
                "readOnly.get(): ${it.origin} annotations from property: ${
                it.annotations.map { it.shortName.asString() }.joinToString(",")
                }"
            )
        }
        readOnly.getter?.receiver?.let { result.add("readOnly.getter.owner: " + nameAndOrigin(it)) }
        readOnly.setter?.let { result.add("readOnly.set(): ${it.origin}") }
        readOnly.setter?.receiver?.let { result.add("readOnly.setter.owner: " + nameAndOrigin(it)) }
        val readWrite =
            ClsClass.declarations.single { it.simpleName.asString() == "readWrite" } as KSPropertyDeclaration
        readWrite.getter?.let { result.add("readWrite.get(): ${it.origin}") }
        readWrite.setter?.let {
            result.add(
                "readWrite.set(): ${it.origin} annotations from property: ${
                it.annotations.map {
                    it.shortName.asString()
                }.joinToString(",")
                }"
            )
        }
        val dataClass = resolver.getClassDeclarationByName(resolver.getKSNameFromString("Data"))!!
        result.add(dataClass.getConstructors().map { it.toString() }.joinToString(","))
        val comp1 = dataClass.declarations.single { it.simpleName.asString() == "comp1" } as KSPropertyDeclaration
        comp1.getter?.let { result.add("comp1.get(): ${it.origin}") }
        comp1.setter?.let { result.add("comp1.set(): ${it.origin}") }
        val comp2 = dataClass.declarations.single { it.simpleName.asString() == "comp2" } as KSPropertyDeclaration
        comp2.getter?.let { result.add("comp2.get(): ${it.origin}") }
        comp2.setter?.let { result.add("comp2.set(): ${it.origin}") }
        val annotationType = comp1.getter?.let {
            result.add(it.annotations.first().annotationType.resolve().declaration.qualifiedName!!.asString())
        }
        val ClassWithoutImplicitPrimaryConstructor =
            resolver.getClassDeclarationByName("ClassWithoutImplicitPrimaryConstructor")!!
        result.add(
            ClassWithoutImplicitPrimaryConstructor.getConstructors().map { it.toString() }.joinToString(",")
        )
        val ImplictConstructorJava = resolver.getClassDeclarationByName("ImplictConstructorJava")!!
        result.add(ImplictConstructorJava.getConstructors().map { it.toString() }.joinToString(","))
        return emptyList()
    }
}
