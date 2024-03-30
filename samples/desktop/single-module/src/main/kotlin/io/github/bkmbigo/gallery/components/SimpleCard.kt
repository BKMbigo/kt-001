package io.github.bkmbigo.gallery.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SimpleCard(
    title: String,
    description: String,
    shape: Shape =  RoundedCornerShape(12.dp),
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        shape = shape
    ) {
        Text(
            text = title,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = description
        )

    }
}

