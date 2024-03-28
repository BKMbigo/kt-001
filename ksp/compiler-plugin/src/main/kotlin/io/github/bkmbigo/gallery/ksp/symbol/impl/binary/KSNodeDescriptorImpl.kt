package io.github.bkmbigo.gallery.ksp.symbol.impl.binary

import io.github.bkmbigo.gallery.ksp.symbol.KSNode
import io.github.bkmbigo.gallery.ksp.symbol.Location
import io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation

abstract class KSNodeDescriptorImpl(override val parent: io.github.bkmbigo.gallery.ksp.symbol.KSNode?) :
    io.github.bkmbigo.gallery.ksp.symbol.KSNode {
    override val location: io.github.bkmbigo.gallery.ksp.symbol.Location =
        io.github.bkmbigo.gallery.ksp.symbol.NonExistLocation
}
