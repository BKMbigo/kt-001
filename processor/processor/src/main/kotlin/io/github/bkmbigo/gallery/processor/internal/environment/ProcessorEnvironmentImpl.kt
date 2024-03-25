package io.github.bkmbigo.gallery.processor.internal.environment

import io.github.bkmbigo.gallery.ksp.processing.KSBuiltIns
import io.github.bkmbigo.gallery.ksp.processing.KSPLogger
import io.github.bkmbigo.gallery.ksp.processing.Resolver
import io.github.bkmbigo.gallery.ksp.symbol.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal class ProcessorEnvironmentImpl(
    override val logger: KSPLogger,
    val resolver: Resolver
): ProcessorEnvironment {
    override val builtIns: KSBuiltIns by lazy {

        resolver.builtIns
    }

    override fun getClassDeclarationByName(name: KSName): KSClassDeclaration? = resolver.getClassDeclarationByName(name)

    override fun getKSNameFromString(name: String): KSName = resolver.getKSNameFromString(name)

}


@OptIn(ExperimentalContracts::class)
internal fun <T> createDefaultProcessorEnvironment(
    logger: KSPLogger,
    resolver: Resolver,
    block: ProcessorEnvironment.() -> T
): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return block(ProcessorEnvironmentImpl(logger, resolver))
}
