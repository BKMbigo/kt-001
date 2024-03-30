package io.github.bkmbigo.gallery

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.github.bkmbigo.gallery.components.SimpleCard
import org.jetbrains.compose.ui.tooling.preview.Preview

@GalleryComponent
@Preview
@Composable
fun SimpleCardComponent(

) {
    SimpleCard(
        title = "Brian",
        description = "I am learning Kotlin",
        shape = RoundedCornerShape(12.dp),
        onClick = { /* no-op */ }
    )
}
