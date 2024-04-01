package io.github.bkmbigo.gallery

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState

@GalleryComponentSelectionScreen
@Composable
fun <T: AbstractGalleryComponent> MyComponentChooser(
    components: List<T>,
    onComponentSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier
    ) {
        items(components) { component ->
            ElevatedCard(
                onClick = {
                    onComponentSelected(component)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = component.componentName
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSplitPaneApi::class)
@GalleryScreen
@Composable
fun MyGalleryScreen(
    component: @Composable () -> Unit,
    stateComponents: @Composable () -> Unit,
    themeStateComponents: @Composable () -> Unit = {}
) {

    var isComponentState by remember { mutableStateOf(false) }

    HorizontalSplitPane(
        splitPaneState = SplitPaneState(
            initialPositionPercentage = 0.5f,
            moveEnabled = true
        )
    ) {
        first {
            component()
        }

        splitter {
            handle {

                val contentColor = LocalContentColor.current

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .border(
                            width = 24.dp,
                            color = contentColor
                        )
                        .drawBehind {
                            drawLine(
                                color = contentColor,
                                start = Offset(
                                    x = 5f,
                                    y = size.height / 4
                                ),
                                end = Offset(
                                    x = size.width - 5f,
                                    y = size.height / 4
                                )
                            )

                            drawLine(
                                color = contentColor,
                                start = Offset(
                                    x = 5f,
                                    y = size.height / 2
                                ),
                                end = Offset(
                                    x = size.width - 5f,
                                    y = size.height / 2
                                )
                            )

                            drawLine(
                                color = contentColor,
                                start = Offset(
                                    x = 5f,
                                    y = size.height * 3 / 4
                                ),
                                end = Offset(
                                    x = size.width - 5f,
                                    y = size.height * 3 / 4
                                )
                            )
                        }
                )

            }

            handle {
            }
        }

        second {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = isComponentState,
                            onClick = { isComponentState = !isComponentState },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = 0,
                                count = 2
                            ),
                            label = {
                                Text("Component")
                            }
                        )
                        SegmentedButton(
                            selected = !isComponentState,
                            onClick = { isComponentState = !isComponentState },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = 1,
                                count = 2
                            ),
                            label = {
                                Text("Theme")
                            }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp))

                if (isComponentState) {
                    stateComponents()
                } else {
                    themeStateComponents()
                }

            }
        }
    }
}
