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

// WITH_RUNTIME
// TEST PROCESSOR: RecordJavaGetAllMembersProcessor
// EXPECTED:
// p1.C: javaSrc/p1/B.java
// p1.D: javaSrc/p1/C.java
// p1.R2: javaSrc/p1/B.java
// p1.R3: javaSrc/p1/C.java
// p1.V2: javaSrc/p1/B.java
// p1.V3: javaSrc/p1/C.java
// END

// FILE: p1/A.kt
package p1;
class A : B {
    fun f1(): R1
    val v1: V1 = TODO()
}

// FILE: p1/B.java
package p1;
public class B extends C {
    R2 f2() { return null }
    V2 v2 = null;
}

// FILE: p1/C.java
package p1;
public class C extends D {
    R3 f3() { return null }
    V3 v3 = null;
}

// FILE: p1/D.kt
package p1;

class D {
    fun f4(): R4
    val v4: V4 = TODO()
}

class R1
class R2
class R3
class R4
class V1
class V2
class V3
class V4
