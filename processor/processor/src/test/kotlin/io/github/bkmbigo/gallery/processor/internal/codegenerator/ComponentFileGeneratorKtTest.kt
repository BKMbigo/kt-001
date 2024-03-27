package io.github.bkmbigo.gallery.processor.internal.codegenerator

import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotation
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ParamWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ScreenComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.StateComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.verifiers.matcher.ComponentMatched
import io.github.bkmbigo.gallery.processor.test.KSExpressionImpl
import io.github.bkmbigo.gallery.processor.test.KSFakeTypeImpl
import io.github.bkmbigo.gallery.processor.test.KSImportDirectiveImpl
import io.github.bkmbigo.gallery.processor.test.KSNameImpl
import org.junit.Test
import kotlin.test.assertEquals


class ComponentFileGeneratorKtTest {

    @Test
    fun `generateComponentScreenFunction generates correct code`() {
        val screenComponentWrapper = ScreenComponentWrapper(
            fqName = KSNameImpl("io.github.bkmbigo.gallery.components.GalleryScreen"),
            componentParameterName = "component",
            stateComponentsParameterName = "stateComponents"
        )

        val componentMatched = ComponentMatched(
            componentName = "",
            fqName = KSNameImpl("io.github.bkmbigo.gallery.components.ProfileComponent"),
            kDoc = null,
            importList = listOf(
                KSImportDirectiveImpl("androidx.compose.runtime.*")
            ),
            parameters = mapOf(
                ParamWrapper(
                    identifier = null,
                    paramName = null,
                    name = KSNameImpl("age"),
                    type = KSFakeTypeImpl(
                        fqName = "kotlin.Int",
                        isNullable = false,
                        isFunctionType = false,
                        isSuspendFunctionType = false,
                        annotations = listOf<KSAnnotation>().asSequence(),
                        arguments = listOf()
                    ),
                    defaultExpression = KSExpressionImpl(
                        """
                            2
                        """.trimIndent()
                    )
                ) to StateComponentWrapper(
                    isRow = false,
                    fqName = KSNameImpl("io.github.bkmbigo.gallery.components.IntStateComponent"),
                    type = KSFakeTypeImpl(
                        fqName = "kotlin.Int",
                        isNullable = false,
                        isFunctionType = false,
                        isSuspendFunctionType = false,
                        annotations = listOf<KSAnnotation>().asSequence(),
                        arguments = listOf()
                    ),
                    isDefault = true,
                    identifier = null,
                    stateParameterName = "state",
                    onStateParameterName = "onState"
                )
            )
        )

        val generatedFile = generateComponentScreenFunction(
            screenComponent = screenComponentWrapper,
            componentMatched = componentMatched
        )

        assertEquals(
            "",
            generatedFile
        )
    }
}
