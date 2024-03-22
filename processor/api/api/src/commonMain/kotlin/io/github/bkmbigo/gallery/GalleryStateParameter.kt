package io.github.bkmbigo.gallery

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class GalleryStateParameter(
    val identifier: String = ""
)
