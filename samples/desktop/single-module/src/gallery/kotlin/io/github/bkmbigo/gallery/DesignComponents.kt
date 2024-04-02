package io.github.bkmbigo.gallery

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.materialkolor.hct.Hct
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.skiko.Cursor
import kotlin.math.roundToInt

@GalleryComponentSelectionScreen
@Composable
fun <T : AbstractGalleryComponent> MyComponentChooser(
    components: List<T>,        // You can either have kotlin.collections.(Mutable)List or kotlinx.collections.immutable.PersistentList
    onComponentSelected: (T) -> Unit
) {
    var searchJob: Job? = remember { null }

    var searchTextFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var displayedComponents by remember { mutableStateOf(components) } // Should be PersistentList

    LaunchedEffect(searchTextFieldValue) {
            searchJob?.cancel()
            searchJob = launch {
                delay(3000)
                if (isActive) {
                    displayedComponents = components.filter {
                        it.componentName.contains(searchTextFieldValue.text)
                    }
                }
            }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchTextFieldValue,
                onValueChange = {
                    searchTextFieldValue = it
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchTextFieldValue.text.isNotBlank()) {
                        IconButton(
                            onClick = {
                                searchTextFieldValue = TextFieldValue("")
                                displayedComponents = components
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Text")
                        }
                    }
                },
                placeholder = {
                    Text(text = "Search...")
                },
                label = {
                    Text(text = "Search")
                }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f),
            contentPadding = PaddingValues(all = 4.dp)
        ) {
            items(displayedComponents) { component ->
                ElevatedCard(
                    onClick = {
                        onComponentSelected(component)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = component.componentName)

                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSplitPaneApi::class)
@GalleryScreen
@Composable
fun MyGalleryScreen(
    hasStateComponents: Boolean,
    hasThemeComponents: Boolean,
    onNavigateBack: () -> Unit,
    componentName: String,
    component: @Composable () -> Unit,
    stateComponents: @Composable () -> Unit,
    themeStateComponents: @Composable () -> Unit = {}
) {

    var isCurrentlyOnThemeComponents by remember {
        mutableStateOf(
            when {
                hasStateComponents && hasThemeComponents -> false
                hasStateComponents -> false
                hasThemeComponents -> true
                else -> true
            }
        )
    }

    Column {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null)
                }
            },
            title = {
                Text(
                    text = componentName
                )
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        VerticalSplitPane(
            splitPaneState = SplitPaneState(
                initialPositionPercentage = if (hasThemeComponents || hasStateComponents) 0.75f else 1f,
                moveEnabled = true
            )
        ) {
            first {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    component()
                }
            }

            splitter {
                visiblePart {
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .fillMaxWidth()
                    )
                }

                handle {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .markAsHandle()
                                .cursorForVerticalResize()
                                .height(9.dp)
                                .width(32.dp)
                                .background(
                                    color = LocalContentColor.current.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(32.dp)
                                )
                        )
                    }
                }
            }

            second {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    if (hasStateComponents || hasThemeComponents) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SingleChoiceSegmentedButtonRow {
                                SegmentedButton(
                                    selected = isCurrentlyOnThemeComponents,
                                    onClick = { isCurrentlyOnThemeComponents = !isCurrentlyOnThemeComponents },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = 0,
                                        count = 2
                                    ),
                                    enabled = hasThemeComponents,
                                    label = {
                                        Text("Theme State", maxLines = 1)
                                    }
                                )
                                SegmentedButton(
                                    selected = !isCurrentlyOnThemeComponents,
                                    onClick = { isCurrentlyOnThemeComponents = !isCurrentlyOnThemeComponents },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = 1,
                                        count = 2
                                    ),
                                    enabled = hasStateComponents,
                                    label = {
                                        Text("Component State", maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    if (!isCurrentlyOnThemeComponents) {
                        stateComponents()
                    } else {
                        themeStateComponents()
                    }

                }
            }
        }

    }


}

@GalleryPageSubstitute<Any>
@Composable
fun MyPageSubstitute(
    paramName: String,
    onNavigateToScreen: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    onNavigateToScreen()
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = paramName)

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null
        )
    }
}

@GalleryStateRow<Boolean>
@Composable
fun BooleanStateComponent(
    paramName: String,
    state: Boolean,
    onStateChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(paramName)

        Spacer(modifier = Modifier.width(4.dp))

        Switch(
            checked = state,
            onCheckedChange = onStateChanged
        )
    }
}

@GalleryStateRow<Boolean?>
@Composable
fun NullableBooleanStateComponent(
    paramName: String,
    state: Boolean?,
    onStateChanged: (Boolean?) -> Unit
) {
    var previousState = remember { state ?: false }

    val switchState by remember(state) {
        mutableStateOf(
            if (state != null) {
                previousState = state
                state
            } else {
                previousState
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(paramName)

        Spacer(modifier = Modifier.width(4.dp))

        Row {
            Switch(
                checked = switchState,
                onCheckedChange = onStateChanged
            )

            Spacer(modifier = Modifier.width(2.dp))

            FilterChip(
                selected = state == null,
                onClick = {
                    if (state != null) {
                        onStateChanged(null)
                    } else {
                        onStateChanged(false)
                    }
                },
                label = {
                    Text("null")
                }
            )
        }
    }
}


@GalleryStateRow<Int>
@Composable
fun IntStateComponent(
    paramName: String,
    state: Int,
    onStateChanged: (Int) -> Unit
) {

    val intState by rememberSaveable(
        state,
        stateSaver = TextFieldValue.Saver
    ) { mutableStateOf(TextFieldValue(state.toString())) }

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = paramName)

        Spacer(modifier = Modifier.width(2.dp))

        OutlinedTextField(
            value = intState,
            onValueChange = { newValue ->
                if (newValue.text.isBlank()) {
                    onStateChanged(0)
                } else {
                    val number = newValue.text.toIntOrNull()
                    if (number != null) {
                        onStateChanged(number)
                    } else {
                        val text = newValue.text
                        text.replace(Regex("[^0-9]"), "")
                        val newNumber = text.toIntOrNull()
                        if (newNumber != null) {
                            onStateChanged(newNumber)
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            maxLines = 1,
            minLines = 1
        )
    }
}


@GalleryStateRow<String>
@Composable
fun StringStateComponent(
    paramName: String,
    state: String,
    onState: (String) -> Unit
) {

    val stringState by rememberSaveable(state, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(state))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(paramName)

        Spacer(modifier = Modifier.width(2.dp))

        OutlinedTextField(
            value = stringState,
            onValueChange = {
                onState(it.text)
            }
        )
    }
}

@GalleryStatePage<Color>
@Composable
fun ColorStateComponent(
    paramName: String,
    state: Color,
    onState: (Color) -> Unit,
    onNavigateBack: () -> Unit
) {

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateBack,
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null
                )
            }

            Text(text = paramName)

            Spacer(modifier = Modifier)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Red")

            Text(text = (state.red * 255.0f).roundToInt().toString())
        }

        Spacer(modifier = Modifier.height(2.dp))

        Slider(
            value = state.red * 256f,
            onValueChange = {
                onState(state.copy(red = it / 255f))
            },
            steps = 256,
            valueRange = 0f..255f
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Green")

            Text(text = (state.green * 256.0f).roundToInt().toString())
        }

        Spacer(modifier = Modifier.height(2.dp))

        Slider(
            value = state.green * 256f,
            onValueChange = {
                onState(state.copy(green = it / 256f))
            },
            steps = 256,
            valueRange = 0f..256f
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Blue")

            Text(text = (state.blue * 255.0f).roundToInt().toString())
        }

        Spacer(modifier = Modifier.height(2.dp))

        Slider(
            value = state.blue * 256f,
            onValueChange = {
                onState(state.copy(blue = it / 256f))
            },
            steps = 256,
            valueRange = 0f..256f
        )
    }

}

internal fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

internal fun Modifier.cursorForVerticalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.N_RESIZE_CURSOR)))
