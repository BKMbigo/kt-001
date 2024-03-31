package io.github.bkmbigo.gallery

/**
 * A @Composable function that shows a selection of available components
 * */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class GalleryComponentSelectionScreen(
    val path: String = ""
)
