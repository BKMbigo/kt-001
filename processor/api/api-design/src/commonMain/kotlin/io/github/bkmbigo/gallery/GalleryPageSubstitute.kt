package io.github.bkmbigo.gallery

/**
 * The component that is displayed when a @GalleryStatePage is to be shown in place of @GalleryStateRow
 * */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class GalleryPageSubstitute<T>
