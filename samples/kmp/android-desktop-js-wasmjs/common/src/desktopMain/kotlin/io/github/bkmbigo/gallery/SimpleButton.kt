package io.github.bkmbigo.gallery

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@GalleryComponent
@Composable
fun SimpleButtonComponent(
    text: String = "Click Me!!"
) {
    SimpleButton(
        text = text,
        onClick = {}
    )
}

@Composable
fun SimpleButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text)
    }
}
