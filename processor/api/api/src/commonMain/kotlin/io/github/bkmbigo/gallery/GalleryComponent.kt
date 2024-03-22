package io.github.bkmbigo.gallery

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class GalleryComponent(
    val componentName: String = "",
    val componentPage: String = ""
)
