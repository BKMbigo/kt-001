package io.github.bkmbigo.gallery

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.bkmbigo.gallery.components.IdeaCard

@GalleryComponent
@Preview
@Composable
fun IdeaCardComponent() {
    IdeaCard(
        idea = ProjectIdea(
            title = "Incremental Compilation for the Kotlin-to-WebAssembly compiler",
            description = "Incremental compilation is a technique that helps increase compilation speed by recompiling only changed files instead of your whole program (also known as performing a clean build). The Kotlin-to-Wasm compiler currently supports only clean builds, but during this project, we will enhance it to support incremental compilation, too.",
            skills = listOf("Kotlin"),
            mentor = "Artem Kozbar, JetBrains"
        ),
        modifier = Modifier,
        onIdeaClicked = {},
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
}
