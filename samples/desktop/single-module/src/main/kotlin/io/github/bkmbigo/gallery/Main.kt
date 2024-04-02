package io.github.bkmbigo.gallery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.bkmbigo.gallery.components.IdeaCard

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "GSOC Idea List"
    ) {
        HomeScreen(
            projectIdeas = listOf(
                ProjectIdea(
                    title = "Incremental compilation for the Kotlin-to-WebAssembly compiler [Hard, 350 hrs]",
                    description = "Incremental compilation is a technique that helps increase compilation speed by recompiling only changed files instead of your whole program (also known as performing a clean build). The Kotlin-to-Wasm compiler currently supports only clean builds, but during this project, we will enhance it to support incremental compilation, too.",
                    skills = listOf(
                        "Kotlin"
                    ),
                    mentor = "Artem Kobzar, JetBrains"
                ),
                ProjectIdea(
                    title = "Compose Multiplatform component gallery generator [Medium, 350 hrs]",
                    description = "Compose Multiplatform is a declarative framework for sharing UIs built with Kotlin across multiple platforms. At the beginning of the React era of web development, Storybook was created, and Storybook’s proposed approach of describing component states and generating the whole UI library gallery is still one of the essential approaches to documentation in web development. Can we do the same with Compose Multiplatform, using it to generate a gallery of web UI elements, as well as galleries for mobile and desktop? Let's give it a try in this project.",
                    skills = listOf(
                        "Kotlin",
                        "Compose",
                        "UI/UX Design"
                    ),
                    mentor = "Artem Kobzar, JetBrains"
                )
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    projectIdeas: List<ProjectIdea> // Should be kotlinx.collections.immutable.PersistentList
) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text("GSOC Project IDEA List")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(
                minSize = 300.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(projectIdeas) { idea ->
                IdeaCard(
                    idea = idea,
                    modifier = Modifier,
                    onIdeaClicked = {

                    }
                )
            }
        }

    }
}
