package io.github.bkmbigo.gallery

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.TYPE)
annotation class GalleryStateParameter(
    val identifier: String = ""
)
