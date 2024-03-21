/*
 * Copyright 2023 Google LLC
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
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
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated

class NestedAnnotationProcessor : AbstractTestProcessor() {
    val result = mutableListOf<String>()

    override fun toResult(): List<String> {
        return result
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val myClass = resolver.getClassDeclarationByName("MyClass")!!
        val param = myClass.primaryConstructor!!.parameters.single { it.name?.asString() == "param" }
        param.annotations.forEach { annotation ->
            result.add("@param: $annotation: ${annotation.annotationType.resolve()}")
        }
        val field = myClass.getDeclaredProperties().single { it.simpleName.asString() == "field" }
        field.annotations.forEach { annotation ->
            result.add("@field $annotation: ${annotation.annotationType.resolve()}")
        }
        val property = myClass.getDeclaredProperties().single { it.simpleName.asString() == "property" }
        property.annotations.forEach { annotation ->
            result.add("@property: $annotation: ${annotation.annotationType.resolve()}")
        }
        property.setter!!.parameter.annotations.forEach { annotation ->
            result.add("@setparam: $annotation: ${annotation.annotationType.resolve()}")
        }

        return emptyList()
    }
}
