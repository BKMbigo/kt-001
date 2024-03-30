package com.github.bkmbigo.gallery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleProfileCard(
    name: String,
    age: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Text(
                text = "Name: $name"
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Age: $age"
            )
        }
    }
}

// This is the component function
@GalleryComponent
@Composable
fun SimpleIconButtonComponent(
    age: Int = 0
) {
    SimpleProfileCard(
        name = "James",
        age = age,
        onClick = { /*TODO*/ }
    )
}
