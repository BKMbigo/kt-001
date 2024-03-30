package io.github.bkmbigo.gallery

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Gallery"
    ) {


    }
}
