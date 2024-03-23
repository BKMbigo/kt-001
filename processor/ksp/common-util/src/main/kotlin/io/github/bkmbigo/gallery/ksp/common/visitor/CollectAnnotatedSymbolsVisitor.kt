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

package io.github.bkmbigo.gallery.ksp.common.visitor

import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotated
import io.github.bkmbigo.gallery.ksp.symbol.KSFile
import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.ksp.symbol.KSPropertyDeclaration
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeAlias
import io.github.bkmbigo.gallery.ksp.symbol.KSTypeParameter
import io.github.bkmbigo.gallery.ksp.symbol.KSValueParameter
import io.github.bkmbigo.gallery.ksp.symbol.KSVisitorVoid

// TODO: Make visitor a generator
class CollectAnnotatedSymbolsVisitor(private val inDepth: Boolean) : KSVisitorVoid() {
    val symbols = arrayListOf<KSAnnotated>()

    override fun visitAnnotated(annotated: KSAnnotated, data: Unit) {
        if (annotated.annotations.any())
            symbols.add(annotated)
    }

    override fun visitFile(file: KSFile, data: Unit) {
        visitAnnotated(file, data)
        file.declarations.forEach { it.accept(this, data) }
    }

    override fun visitTypeAlias(typeAlias: KSTypeAlias, data: Unit) {
        if (typeAlias.annotations.any())
            symbols.add(typeAlias)
    }

    override fun visitClassDeclaration(classDeclaration: io.github.bkmbigo.gallery.ksp.symbol.KSClassDeclaration, data: Unit) {
        visitAnnotated(classDeclaration, data)
        classDeclaration.typeParameters.forEach { it.accept(this, data) }
        classDeclaration.declarations.forEach { it.accept(this, data) }
    }

    override fun visitPropertyGetter(getter: io.github.bkmbigo.gallery.ksp.symbol.KSPropertyGetter, data: Unit) {
        visitAnnotated(getter, data)
        if (inDepth) {
            getter.declarations.forEach { it.accept(this, data) }
        }
    }

    override fun visitPropertySetter(setter: io.github.bkmbigo.gallery.ksp.symbol.KSPropertySetter, data: Unit) {
        setter.parameter.accept(this, data)
        visitAnnotated(setter, data)
        if (inDepth) {
            setter.declarations.forEach { it.accept(this, data) }
        }
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        visitAnnotated(function, data)
        function.typeParameters.forEach { it.accept(this, data) }
        function.parameters.forEach { it.accept(this, data) }
        if (inDepth) {
            function.declarations.forEach { it.accept(this, data) }
        }
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        visitAnnotated(property, data)
        property.typeParameters.forEach { it.accept(this, data) }
        property.getter?.accept(this, data)
        property.setter?.accept(this, data)
    }

    override fun visitTypeParameter(typeParameter: KSTypeParameter, data: Unit) {
        visitAnnotated(typeParameter, data)
        super.visitTypeParameter(typeParameter, data)
    }

    override fun visitValueParameter(valueParameter: KSValueParameter, data: Unit) {
        visitAnnotated(valueParameter, data)
    }
}
