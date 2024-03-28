package io.github.bkmbigo.gallery.ksp.common.impl

import io.github.bkmbigo.gallery.ksp.common.KSObjectCache
import io.github.bkmbigo.gallery.ksp.symbol.KSName

class KSNameImpl private constructor(val name: String) : KSName {
    companion object : KSObjectCache<String, KSNameImpl>() {
        fun getCached(name: String) = cache.getOrPut(name) { KSNameImpl(name) }
    }

    override fun asString(): String {
        return name
    }

    override fun getQualifier(): String {
        return name.split(".").dropLast(1).joinToString(".")
    }

    override fun getShortName(): String {
        return name.split(".").last()
    }
}
