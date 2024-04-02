package io.github.bkmbigo.gallery

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import io.github.bkmbigo.gallery.components.SearchField

@GalleryComponent
@Composable
fun SearchFieldComponent(
    showLeadingIcon: Boolean = true,
    buttonBackgroundColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    var searchText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    SearchField(
        text = searchText,
        onTextChange = {
            searchText = it
        },
        onSearch = {},
        modifier = Modifier,
        showLeadingIcon = showLeadingIcon,
        buttonBackgroundColor = buttonBackgroundColor,
        placeholderText = "Search"
    )
}
