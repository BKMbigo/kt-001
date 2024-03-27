package io.github.bkmbigo.gallery.processor.test

import io.github.bkmbigo.gallery.ksp.symbol.KSName

class KSNameImpl(private val fqName: String): KSName {
    override fun asString(): String = fqName

    override fun getQualifier(): String = fqName.substringBeforeLast(".", "")

    override fun getShortName(): String = fqName.substringAfterLast(".")

}
