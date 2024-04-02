package io.github.bkmbigo.gallery.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TopAppBar as MaterialTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    pageTitle: String,
    onNavigateBack: () -> Unit,
    onOptionsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    MaterialTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = pageTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = onOptionsClicked) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Back")
            }
        }
    )
}
