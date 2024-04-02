package io.github.bkmbigo.gallery.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.bkmbigo.gallery.ProjectIdea

@Composable
fun IdeaCard(
    idea: ProjectIdea,
    onIdeaClicked: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = idea.title,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = idea.description,
                fontSize = 12.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier
                        .weight(1f, true)
                ) {
                    items(idea.skills, key = { it }) { skill ->
                        ElevatedSuggestionChip(
                            onClick = {},
                            label = {
                                Text(text = skill)
                            },
                            elevation = SuggestionChipDefaults.suggestionChipElevation(
                                elevation = 4.dp
                            ),
                            modifier = Modifier
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier
                        .requiredWidth(IntrinsicSize.Max)
                ) {
                    Text("Mentor:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(idea.mentor, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Preview
@Composable
private fun IdeaCardPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        IdeaCard(
            idea = ProjectIdea(
                title = "Incremental compilation for the Kotlin-to-WebAssembly compiler [Hard, 350 hrs]",
                description = "Incremental compilation is a technique that helps increase compilation speed by recompiling only changed files instead of your whole program (also known as performing a clean build). The Kotlin-to-Wasm compiler currently supports only clean builds, but during this project, we will enhance it to support incremental compilation, too.",
                skills = listOf(
                    "Kotlin"
                ),
                mentor = "Artem Kobzar, JetBrains"
            ),
            onIdeaClicked = {},
        )
    }
}
