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

// TEST PROCESSOR: DeclarationPackageNameProcessor
// EXPECTED:
// <no name>:J1
// <no name>:J1.<init>
// <no name>:J2
// <no name>:J2.<init>
// <no name>:K1
// <no name>:K1.<init>
// <no name>:K2
// <no name>:K2.<init>
// lib:H1
// lib:H1.<init>
// test.java.pack:C
// test.java.pack:C.<init>
// test.java.pack:Inner
// test.java.pack:Inner.<init>
// test.java.pack:Nested
// test.java.pack:Nested.<init>
// test.pack:Inner
// test.pack:Inner.<init>
// test.pack:Inner.innerFoo
// test.pack:InnerLocal
// test.pack:InnerLocal.<init>
// test.pack:Nested
// test.pack:Nested.<init>
// test.pack:Nested.nestedFoo
// test.pack:Outer
// test.pack:Outer.<init>
// test.pack:Outer.Foo
// test.pack:Val
// test.pack:a
// test.pack:innerVal
// test.pack:nestedVal
// END

// MODULE: module1
// FILE: H1.kt
package lib
class H1

// FILE: K1.kt
class K1

// FILE: J1.java
class J1 {
}

// MODULE: main(module1)
// FILE: K2.kt
class K2

// FILE: J2.java
class J2 {
}

// FILE: a.kt
package test.pack

class Outer {
    val Val

    fun Foo() {}

    inner class Inner {
        val innerVal: Int
        fun innerFoo() {
            class InnerLocal
        }
    }
    class Nested {
        private val nestedVal: Int
        fun nestedFoo() {
            val a = 1
        }
    }
}

//FILE: test/java/pack/C.java
package test.java.pack;

public class C {
    class Inner {

    }

    static class Nested  {}
}
