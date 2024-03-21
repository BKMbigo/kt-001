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

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSTopDownVisitor

open class FunctionTypeAliasProcessor : AbstractTestProcessor() {
    val results = mutableListOf<String>()
    val typeRefCollector = RefCollector()
    val refs = mutableSetOf<KSTypeReference>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val files = resolver.getNewFiles()

        files.forEach {
            it.accept(typeRefCollector, refs)
        }

        val types = refs.mapNotNull { it.resolve() }.sortedBy { it.toString() }.distinctBy { it.toString() }

        for (i in types) {
            for (j in types) {
                results.add("$i ?= $j : ${i.isAssignableFrom(j)} / ${i == j}")
            }
        }
        return emptyList()
    }

    override fun toResult(): List<String> {
        return results
    }
}

open class RefCollector : KSTopDownVisitor<MutableCollection<KSTypeReference>, Unit>() {
    override fun defaultHandler(node: KSNode, data: MutableCollection<KSTypeReference>) = Unit

    override fun visitTypeReference(typeReference: KSTypeReference, data: MutableCollection<KSTypeReference>) {
        super.visitTypeReference(typeReference, data)
        data.add(typeReference)
    }
}
