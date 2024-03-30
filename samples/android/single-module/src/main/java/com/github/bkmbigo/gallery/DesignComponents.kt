package com.github.bkmbigo.gallery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.bkmbigo.gallery.GalleryScreen
import io.github.bkmbigo.gallery.GalleryStateRow

@OptIn(ExperimentalMaterial3Api::class)
@GalleryScreen
@Composable
fun GalleryComponentScreen(
    onNavigateBack: () -> Unit = {},
    component: @Composable () -> Unit,
    stateComponents: @Composable () -> Unit,
    themeStateComponents: @Composable () -> Unit = {}
) {

    var screenState by remember { mutableStateOf(ScreenState.Component) }

    // This is temporary, it should be a custom layout at least, to handle different configuration sizes
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    if (screenState != ScreenState.Component) {
                        IconButton(
                            onClick = {
                                screenState = ScreenState.Component
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                onNavigateBack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = null
                            )
                        }
                    }
                },
                title = {
                    Text("Component")
                },
                actions = {

                    if (screenState == ScreenState.Component) {
                        IconButton(
                            onClick = {
                                screenState = ScreenState.ScreenState
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = {
                                screenState = ScreenState.ThemeState
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null
                            )
                        }
                    }

                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (screenState) {
                ScreenState.Component -> {
                    component()
                }
                ScreenState.ScreenState -> {
                    stateComponents()
                }
                ScreenState.ThemeState -> {
                    themeStateComponents()
                }
            }
        }
    }

}

private enum class ScreenState {
    Component,
    ScreenState,
    ThemeState
}

// This is a @GalleryStateComponent. It is used to change Int parameters on Components
@GalleryStateRow<Int>
@Composable
fun IntStateComponent(
    state: Int,
    onState: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val intText by rememberSaveable(state, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(state.toString()))
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Int")

        Spacer(modifier = Modifier.width((4.dp)))

        OutlinedTextField(
            value = intText,
            onValueChange = { newValue ->
                // Try converting to Int
                newValue.text.toIntOrNull()?.let {
                    onState(it)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
    }
}

@GalleryStateRow<String>
@Composable
fun StringStateComponent(
    state: String,
    onState: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val textFieldValue by rememberSaveable(state, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(state))
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "String")

        Spacer(modifier = Modifier.width(4.dp))

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {
                onState(it.text)
            }
        )
    }
}
