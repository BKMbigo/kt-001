package com.github.bkmbigo.gallery

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.bkmbigo.gallery.GalleryComponent

fun getName() = "Hello"

val savedName = "Hello"

@GalleryComponent
@Composable
fun SimpleIconButtonComponent(
    text: String = getName(),
    otherText: String = "",
    savedText: String = savedName,
    ifText: String = if (true) {
        savedText
    } else {
        "saved"
    }
) {
    SimpleIconButton(
        icon = Icons.Default.Add,
        onClick = { /*no-op*/ }
    )
}

@Composable
fun SimpleIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(icon, contentDescription = null)
    }
}
