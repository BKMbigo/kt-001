package io.github.bkmbigo.gallery.processor.internal

import io.github.bkmbigo.gallery.GalleryComponentTheme as GalleryComponentThemeAnnotation
import io.github.bkmbigo.gallery.GalleryScreen as GalleryScreenAnnotation
import io.github.bkmbigo.gallery.GalleryComponentSelectionScreen as GalleryComponentSelectionScreenAnnotation
import io.github.bkmbigo.gallery.GalleryPageSubstitute as GalleryPageSubstituteAnnotation
import io.github.bkmbigo.gallery.GalleryMultiSelectableElementPage as GalleryMultiSelectablePageAnnotation
import io.github.bkmbigo.gallery.GallerySingleSelectableElementPage as GallerySingleSelectablePageAnnotation
import io.github.bkmbigo.gallery.GalleryStateParameter as GalleryStateParameterAnnotation
import io.github.bkmbigo.gallery.GalleryComponent as GalleryComponentAnnotation
import io.github.bkmbigo.gallery.GalleryStateComponent as GalleryStateComponentAnnotation
import io.github.bkmbigo.gallery.GalleryStatePage as GalleryStatePageAnnotation
import io.github.bkmbigo.gallery.GalleryStateRow as GalleryStateRowAnnotation

internal object Constants {

    internal object Annotations {

        internal object SimpleName {
            val GalleryComponent = GalleryComponentAnnotation::class.simpleName!!
            val GalleryStateComponent = GalleryStateComponentAnnotation::class.simpleName!!
            val GalleryStatePage = GalleryStatePageAnnotation::class.simpleName!!
            val GalleryStateRow = GalleryStateRowAnnotation::class.simpleName!!
            val GalleryStateParameter = GalleryStateParameterAnnotation::class.simpleName!!
            val GalleryComponentTheme = GalleryComponentThemeAnnotation::class.simpleName!!

            // Design Annotations
            val GallerySingleSelectablePage = GallerySingleSelectablePageAnnotation::class.simpleName!!
            val GalleryMultiSelectablePage = GalleryMultiSelectablePageAnnotation::class.simpleName!!
            val GalleryPageSubstitute = GalleryPageSubstituteAnnotation::class.simpleName!!
            val GalleryScreen = GalleryScreenAnnotation::class.simpleName!!
            val GalleryComponentSelectionScreen = GalleryComponentSelectionScreenAnnotation::class.simpleName!!

            // Extra Annotations
            val Composable = "Composable"
        }

        internal object FQName {
            val GalleryComponent = GalleryComponentAnnotation::class.qualifiedName!!
            val GalleryStateComponent = GalleryStateComponentAnnotation::class.qualifiedName!!
            val GalleryStatePage = GalleryStatePageAnnotation::class.qualifiedName!!
            val GalleryStateRow = GalleryStateRowAnnotation::class.qualifiedName!!
            val GalleryStateParameter = GalleryStateParameterAnnotation::class.qualifiedName!!
            val GalleryComponentTheme = GalleryComponentThemeAnnotation::class.qualifiedName!!

            // Design Annotations
            val GallerySingleSelectablePage = GallerySingleSelectablePageAnnotation::class.qualifiedName!!
            val GalleryMultiSelectablePage = GalleryMultiSelectablePageAnnotation::class.qualifiedName!!
            val GalleryPageSubstitute = GalleryPageSubstituteAnnotation::class.qualifiedName!!
            val GalleryScreen = GalleryScreenAnnotation::class.qualifiedName!!
            val GalleryComponentSelectionScreen = GalleryComponentSelectionScreenAnnotation::class.qualifiedName!!

            val Composable = "androidx.compose.runtime.Composable"
        }

    }

}
