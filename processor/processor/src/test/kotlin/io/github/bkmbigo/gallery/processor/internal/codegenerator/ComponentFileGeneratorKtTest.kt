package io.github.bkmbigo.gallery.processor.internal.codegenerator

import io.github.bkmbigo.gallery.ksp.KspExperimental
import io.github.bkmbigo.gallery.ksp.processing.KSBuiltIns
import io.github.bkmbigo.gallery.ksp.processing.KSDirectoryOptions
import io.github.bkmbigo.gallery.ksp.processing.KSPLogger
import io.github.bkmbigo.gallery.ksp.processing.Resolver
import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironmentImpl
import io.github.bkmbigo.gallery.processor.internal.models.ComponentRegistrar
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.PageSubstituteWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ParamWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ScreenComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.StateComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.verifiers.matcher.ComponentMatched
import io.github.bkmbigo.gallery.processor.test.KSExpressionImpl
import io.github.bkmbigo.gallery.processor.test.KSFakeTypeImpl
import io.github.bkmbigo.gallery.processor.test.KSImportDirectiveImpl
import io.github.bkmbigo.gallery.processor.test.KSNameImpl
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals


class ComponentFileGeneratorKtTest {
    private lateinit var fakeComponentRegistrar: ComponentRegistrar
    private lateinit var processingEnvironment: ProcessorEnvironment

    @Before
    fun setUpProcessingEnvironment() {
        val fakeLogger = object: KSPLogger {
            val errors = mutableListOf<String>()
            val warnings = mutableListOf<String>()

            override fun error(message: String, symbol: KSNode?) {
                errors.add(message)
            }

            override fun exception(e: Throwable) {
                TODO("Not yet implemented")
            }

            override fun info(message: String, symbol: KSNode?) {
                TODO("Not yet implemented")
            }

            override fun logging(message: String, symbol: KSNode?) {
                TODO("Not yet implemented")
            }

            override fun warn(message: String, symbol: KSNode?) {
                warnings.add(message)
            }
        }

        val fakeResolver = object: Resolver {
            override val builtIns: KSBuiltIns
                get() = object: KSBuiltIns {
                    override val annotationType: KSType
                        get() = TODO("Not yet implemented")
                    override val anyType: KSType
                        get() = TODO("Not yet implemented")
                    override val arrayType: KSType
                        get() = TODO("Not yet implemented")
                    override val booleanType: KSType
                        get() = TODO("Not yet implemented")
                    override val byteType: KSType
                        get() = TODO("Not yet implemented")
                    override val charType: KSType
                        get() = TODO("Not yet implemented")
                    override val doubleType: KSType
                        get() = TODO("Not yet implemented")
                    override val floatType: KSType
                        get() = TODO("Not yet implemented")
                    override val intType: KSType
                        get() = TODO("Not yet implemented")
                    override val iterableType: KSType
                        get() = TODO("Not yet implemented")
                    override val longType: KSType
                        get() = TODO("Not yet implemented")
                    override val nothingType: KSType
                        get() = TODO("Not yet implemented")
                    override val numberType: KSType
                        get() = TODO("Not yet implemented")
                    override val shortType: KSType
                        get() = TODO("Not yet implemented")
                    override val stringType: KSType
                        get() = TODO("Not yet implemented")
                    override val unitType: KSType
                        get() = TODO("Not yet implemented")
                }
            override val directoryOptions: KSDirectoryOptions
                get() = object: KSDirectoryOptions {
                    override val cachesDir: File
                        get() = TODO("Not yet implemented")
                    override val kotlinOutputDir: File
                        get() = TODO("Not yet implemented")
                    override val resourceOutputDir: File
                        get() = TODO("Not yet implemented")
                }

            override fun createKSTypeReferenceFromKSType(type: KSType): KSTypeReference {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun effectiveJavaModifiers(declaration: KSDeclaration): Set<Modifier> {
                TODO("Not yet implemented")
            }

            override fun getAllFiles(): Sequence<KSFile> {
                TODO("Not yet implemented")
            }

            override fun getClassDeclarationByName(name: KSName): KSClassDeclaration? {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun getDeclarationsFromPackage(packageName: String): Sequence<KSDeclaration> {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun getDeclarationsInSourceOrder(container: KSDeclarationContainer): Sequence<KSDeclaration> {
                TODO("Not yet implemented")
            }

            override fun getFunctionDeclarationsByName(
                name: KSName,
                includeTopLevel: Boolean
            ): Sequence<KSFunctionDeclaration> {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun getJavaWildcard(reference: KSTypeReference): KSTypeReference {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun getJvmCheckedException(function: KSFunctionDeclaration): Sequence<KSType> {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun getJvmCheckedException(accessor: KSPropertyAccessor): Sequence<KSType> {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun getJvmName(declaration: KSFunctionDeclaration): String? {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun getJvmName(accessor: KSPropertyAccessor): String? {
                TODO("Not yet implemented")
            }

            override fun getKSNameFromString(name: String): KSName {
                TODO("Not yet implemented")
            }

            override fun getNewFiles(): Sequence<KSFile> {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun getOwnerJvmClassName(declaration: KSFunctionDeclaration): String? {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun getOwnerJvmClassName(declaration: KSPropertyDeclaration): String? {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun getPackageAnnotations(packageName: String): Sequence<KSAnnotation> {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun getPackagesWithAnnotation(annotationName: String): Sequence<String> {
                TODO("Not yet implemented")
            }

            override fun getPropertyDeclarationByName(name: KSName, includeTopLevel: Boolean): KSPropertyDeclaration? {
                TODO("Not yet implemented")
            }

            override fun getSymbolsWithAnnotation(annotationName: String, inDepth: Boolean): Sequence<KSAnnotated> {
                TODO("Not yet implemented")
            }

            override fun getTypeArgument(typeRef: KSTypeReference, variance: Variance): KSTypeArgument {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun isJavaRawType(type: KSType): Boolean {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun mapJavaNameToKotlin(javaName: KSName): KSName? {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun mapKotlinNameToJava(kotlinName: KSName): KSName? {
                TODO("Not yet implemented")
            }

            @KspExperimental
            override fun mapToJvmSignature(declaration: KSDeclaration): String? {
                TODO("Not yet implemented")
            }

            override fun overrides(overrider: KSDeclaration, overridee: KSDeclaration): Boolean {
                TODO("Not yet implemented")
            }

            override fun overrides(
                overrider: KSDeclaration,
                overridee: KSDeclaration,
                containingClass: KSClassDeclaration
            ): Boolean {
                TODO("Not yet implemented")
            }
        }

        processingEnvironment = ProcessorEnvironmentImpl(
            logger = fakeLogger,
            resolver = fakeResolver
        )
    }

    @Before
    fun setup() {
        with(processingEnvironment) {
            fakeComponentRegistrar = ComponentRegistrar().apply {
                registerGenericPageSubstitute(
                    PageSubstituteWrapper(
                        fqName = KSNameImpl("io.github.bkmbigo.gallery.MyPgeSubstitute"),
                        type = KSFakeTypeImpl(
                            fqName = "kotlin.Any",
                            isNullable = true
                        ),
                        paramNameParameterName = null,
                        onNavigateToScreenParameterName = "onNavigateToScreen"
                    )
                )
            }
        }
    }

    // This test intentionally fails
    @Test
    fun `generateComponentScreenFunction generates correct code`() {
        val screenComponentWrapper = ScreenComponentWrapper(
            fqName = KSNameImpl("io.github.bkmbigo.gallery.components.GalleryScreen"),
            componentParameterName = "component",
            stateComponentsParameterName = "stateComponents",
            onNavigateBackParameterName = "onNavigateBack",
            themeStateComponentsParameterName = null,
            componentNameParameterName = null,
            hasThemeComponentParameterName = "hasThemeComponents",
            hasStateComponentsParameterName = "hasScreenComponents"
        )

        val componentMatched = ComponentMatched(
            componentName = "ProfileComponent",
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
                    isRow = true,
                    fqName = KSNameImpl("io.github.bkmbigo.gallery.components.IntStateRowComponent"),
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
                    onStateParameterName = "onState",
                    paramNameParameterName = "paramName",
                    onNavigateBackParameterName = null
                ),
                ParamWrapper(
                    identifier = null,
                    paramName = null,
                    name = KSNameImpl("dadAge"),
                    type = KSFakeTypeImpl(
                        fqName = "kotlin.Int",
                        isNullable = true
                    ),
                    defaultExpression = KSExpressionImpl(
                        """
                            34
                        """.trimIndent()
                    )
                ) to StateComponentWrapper(
                    isRow = false,
                    fqName = KSNameImpl("io.github.bkmbigo.gallery.components.IntStatePageComponent"),
                    type = KSFakeTypeImpl(
                        fqName = "kotlin.Int",
                        isNullable = false,
                    ),
                    isDefault = true,
                    identifier = null,
                    stateParameterName = "state",
                    onStateParameterName = "onState",
                    paramNameParameterName = "paramName",
                    onNavigateBackParameterName = "onNavigateBack"
                )
            )
        )

        val generatedFile = generateComponentScreenFunction(
            componentRegistrar = fakeComponentRegistrar,
            screenComponent = screenComponentWrapper,
            componentMatched = componentMatched
        )

        assertEquals(
            "",
            generatedFile
        )
    }
}
