package io.github.bkmbigo.gallery.processor

import io.github.bkmbigo.gallery.ksp.processing.*
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotated
import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.processor.internal.Constants

class GalleryProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
): SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver
            .getSymbolsWithAnnotation(Constants.Annotations.FQName.GalleryComponent)
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach {
                logger.error("""
                    The node is ${it.simpleName.getShortName()} with parameters
                        ${it.parameters.joinToString("\n\t") { "Parameter: ${it.name?.getShortName()}\nisCallExpression: ${it.defaultExpression?.isCallExpression}, isNameReferenceElement: ${it.defaultExpression?.isNameReferenceExpression} ,isReferenceElement: ${it.defaultExpression?.isReferenceExpression}\ntext: ${it.defaultExpression?.getExpressionAsString()}" }}
                """.trimIndent(), it)
            }


        return emptyList()
    }
}
