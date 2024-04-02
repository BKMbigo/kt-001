package io.github.bkmbigo.gallery

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.bkmbigo.gallery.components.TopScreenBar

@GalleryComponent
@Preview
@Composable
fun TopScreenBarComponent() {
    TopScreenBar(
        pageTitle = "Project Ideas",
        onNavigateBack = {

        },
        onOptionsClicked = {

        },
        modifier = Modifier
            .fillMaxWidth()
    )
}
