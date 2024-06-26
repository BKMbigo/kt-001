package io.github.bkmbigo.gallery.processor.internal.utils

import io.github.bkmbigo.gallery.ksp.symbol.KSType
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment

context(ProcessorEnvironment)
val KSType?.isAny
    get() = this == builtIns.anyType

context(ProcessorEnvironment)
val KSType?.isUnit
    get() = this == builtIns.unitType

context(ProcessorEnvironment)
val KSType.isNothing
    get() = this == builtIns.nothingType

context(ProcessorEnvironment)
val KSType.isBoolean
    get() = this == builtIns.booleanType

context(ProcessorEnvironment)
val KSType.isChar
    get() = this == builtIns.charType

context(ProcessorEnvironment)
val KSType.isString
    get() = this == builtIns.stringType

context(ProcessorEnvironment)
val KSType.isInt
    get() = this == builtIns.intType

context(ProcessorEnvironment)
val KSType.isDouble
    get() = this == builtIns.doubleType

context(ProcessorEnvironment)
val KSType.isFloat
    get() = this == builtIns.floatType

context(ProcessorEnvironment)
val KSType.isByte
    get() = this == builtIns.byteType

context(ProcessorEnvironment)
val KSType.isShort
    get() = this == builtIns.shortType

context(ProcessorEnvironment)
val KSType.isNumber
    get() = this == builtIns.numberType

context(ProcessorEnvironment)
val KSType.isKotlinList
    get() = this.declaration.qualifiedName?.asString() == "kotlin.collections.List"

context(ProcessorEnvironment)
val KSType.isKotlinMutableList
    get() = this.declaration.qualifiedName?.asString() == "kotlin.collections.MutableList"

context(ProcessorEnvironment)
val KSType.isKotlinSet
    get() = this.declaration.qualifiedName?.asString() == "kotlin.collections.Set"

context(ProcessorEnvironment)
val KSType.isKotlinMutableSet
    get() = this.declaration.qualifiedName?.asString() == "kotlin.collections.MutableSet"

context(ProcessorEnvironment)
val KSType.isKotlinPersistentList
    get() = this.declaration.qualifiedName?.asString() == "kotlinx.collections.immutable.PersistentList"
