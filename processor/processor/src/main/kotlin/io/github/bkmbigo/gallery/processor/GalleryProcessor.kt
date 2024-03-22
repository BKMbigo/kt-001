package io.github.bkmbigo.gallery.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import io.github.bkmbigo.gallery.processor.internal.Constants

class GalleryProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
): SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver
            .getSymbolsWithAnnotation(Constants.Annotations.FQName.GalleryComponent)
            .forEach { logger.error("@GalleryComponentLocated found", it) }

//        throw IllegalArgumentException()

        // We don't postpone any symbols to the next processing round!!!
        return emptyList()
    }
}
