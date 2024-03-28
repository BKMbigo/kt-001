package com.github.bkmbigo.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.bkmbigo.gallery.GalleryComponent
import io.github.bkmbigo.gallery.GalleryStatePage
import io.github.bkmbigo.gallery.GalleryStateParameter
import io.github.bkmbigo.gallery.GalleryStateRow


// This is the component we want to add to our gallery
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

// This is the component function
@GalleryComponent
@Composable
fun SimpleIconButtonComponent(
    text: String = "",
    otherText: String = "",
    savedText: String = ""
) {
    SimpleIconButton(
        icon = Icons.Default.Add,
        onClick = { /*no-op*/ }
    )
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
