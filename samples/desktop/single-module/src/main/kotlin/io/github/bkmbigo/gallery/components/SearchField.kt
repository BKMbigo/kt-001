package io.github.bkmbigo.gallery.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun SearchField(
    text: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onSearch: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    showLeadingIcon: Boolean = true,
    buttonBackgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    placeholderText: String = "Search"
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = text,
            onValueChange = {
                onTextChange(it)

                coroutineScope.launch {
                    delay(3000)
                    if (isActive) {
                        onSearch(text.text)
                    }
                }
            },
            leadingIcon = {
                if (showLeadingIcon) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            },
            trailingIcon = {
                if (text.text.isNotBlank()) {
                    IconButton(
                        onClick = {
                            onTextChange(TextFieldValue(""))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            placeholder = {
                Text(text = placeholderText)
            }
        )

        Spacer(modifier = Modifier.width(4.dp))

        Button(
            onClick = {
                onSearch(text.text)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonBackgroundColor
            )
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )

            Spacer(modifier = Modifier.width(2.dp))

            Text(
                text = "Search",
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
    }
}
