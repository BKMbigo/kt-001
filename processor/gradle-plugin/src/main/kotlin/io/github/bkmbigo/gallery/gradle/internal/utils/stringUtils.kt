package io.github.bkmbigo.gallery.gradle.internal.utils

import org.gradle.kotlin.dsl.support.uppercaseFirstChar

internal fun joinDashLowercaseNonEmpty(vararg parts: String): String =
    parts
        .filter { it.isNotEmpty() }
        .joinToString(separator = "-") { it.lowercase() }

internal fun String.sanitizePackageName(): String {
    val parts = split("-")
    return buildString {
        parts.forEachIndexed { index, s ->
            if (index > 0) {
                append(s.uppercaseFirstChar())
            } else {
                append(s)
            }
        }
    }
}
