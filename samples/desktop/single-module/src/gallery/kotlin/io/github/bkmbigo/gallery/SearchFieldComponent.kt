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
import org.jetbrains.compose.ui.tooling.preview.Preview

@GalleryComponent
@Preview
@Composable
fun SearchFieldComponent() {

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
        showLeadingIcon = true,
        buttonBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
        placeholderText = "Search"
    )
}
